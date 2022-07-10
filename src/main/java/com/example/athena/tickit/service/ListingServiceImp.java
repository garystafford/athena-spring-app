package com.example.athena.tickit.service;

import com.example.athena.common.AthenaClientFactory;
import com.example.athena.common.AthenaCommon;
import com.example.athena.config.ConfigProperties;
import com.example.athena.tickit.model.ecomm.Listing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.*;
import software.amazon.awssdk.services.athena.paginators.GetQueryResultsIterable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

@Service
public class ListingServiceImp implements ListingService {

    private static final Logger logger = LoggerFactory.getLogger(ListingServiceImp.class);

    private final ConfigProperties configProperties;

    private final AthenaClientFactory athenaClientFactory;

    private final AthenaCommon athenaCommon;

    @Autowired
    public ListingServiceImp(ConfigProperties configProperties, AthenaClientFactory athenaClientFactory, AthenaCommon athenaCommon) {
        this.configProperties = configProperties;
        this.athenaClientFactory = athenaClientFactory;
        this.athenaCommon = athenaCommon;
    }

    public List<Listing> findAll(Integer limit, Integer offset) {

        if (limit == null || limit < 1 || limit > configProperties.getLimit()) {
            limit = configProperties.getLimit();
        }

        if (offset == null || offset < 1) {
            offset = 0;
        }

        String whereClause = "WHERE listid IS NOT NULL";

        String query = String.format("""
                SELECT *
                FROM refined_tickit_public_listing
                %s
                ORDER BY listid
                OFFSET %s
                LIMIT %s;""", whereClause, offset, limit);

        return startQuery(query);
    }

    public Listing findById(int id) {
        String query = String.format("""
                SELECT  DISTINCT *
                FROM refined_tickit_public_listing
                WHERE listid=%s""", id);

        Listing listing;
        try {
            listing = startQuery(query).get(0);
        } catch (IndexOutOfBoundsException e) {
            logger.error(e.getMessage());
            return null;
        }
        return listing;
    }

    private List<Listing> startQuery(String query) {

        logger.debug(String.format("Query: %s", query.replace("\n", " ")));

        AthenaClient athenaClient = athenaClientFactory.createClient();
        String queryExecutionId = athenaCommon.submitAthenaQuery(athenaClient, query);
        athenaCommon.waitForQueryToComplete(athenaClient, queryExecutionId);
        List<Listing> listings = processResultRows(athenaClient, queryExecutionId);
        athenaClient.close();

        return listings;
    }

    private List<Listing> processResultRows(AthenaClient athenaClient, String queryExecutionId) {

        List<Listing> listings = new ArrayList<>();

        try {
            // Max Results can be set but if it's not set,
            // it will choose the maximum page size
            GetQueryResultsRequest getQueryResultsRequest = GetQueryResultsRequest.builder()
                    .queryExecutionId(queryExecutionId).build();

            GetQueryResultsIterable getQueryResultsResults = athenaClient.getQueryResultsPaginator(getQueryResultsRequest);

            List<Row> rows;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

            for (GetQueryResultsResponse result : getQueryResultsResults) {
                rows = result.resultSet().rows();

                for (Row myRow : rows.subList(1, rows.size())) { // skip first row - column names
                    List<Datum> allData = myRow.data();
                    Listing listing = new Listing();
                    listing.setId(parseInt(allData.get(0).varCharValue()));
                    listing.setSellerId(parseInt(allData.get(1).varCharValue()));
                    listing.setEventId(parseInt(allData.get(2).varCharValue()));
                    listing.setDateId(parseInt(allData.get(3).varCharValue()));
                    listing.setNumTickets(Integer.parseInt(allData.get(4).varCharValue()));
                    listing.setPricePerTicket(new BigDecimal(allData.get(5).varCharValue()));
                    listing.setListTime(LocalDateTime.parse(allData.get(6).varCharValue(), formatter));
                    listings.add(listing);
                }
            }
        } catch (AthenaException e) {
            logger.error(e.getMessage());
        }
        return listings;
    }
}
