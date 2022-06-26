package com.example.athena.tickit.service;

import com.example.athena.common.AthenaClientFactory;
import com.example.athena.common.AthenaCommon;
import com.example.athena.config.ConfigProperties;
import com.example.athena.tickit.model.saas.Venue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.*;
import software.amazon.awssdk.services.athena.paginators.GetQueryResultsIterable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

@Service
public class VenueServiceImp implements VenueService {

    private static final Logger logger = LoggerFactory.getLogger(VenueServiceImp.class);

    private final ConfigProperties configProperties;

    private final AthenaClientFactory athenaClientFactory;

    private final AthenaCommon athenaCommon;

    @Autowired
    public VenueServiceImp(ConfigProperties configProperties, AthenaClientFactory athenaClientFactory, AthenaCommon athenaCommon) {
        this.configProperties = configProperties;
        this.athenaClientFactory = athenaClientFactory;
        this.athenaCommon = athenaCommon;
    }

    public List<Venue> findAll(Integer limit, Integer offset) {

        if (limit == null || limit < 1 || limit > configProperties.getLimit()) {
            limit = configProperties.getLimit();
        }

        if (offset == null || offset < 1) {
            offset = 0;
        }

        String whereClause = "WHERE venueid IS NOT NULL";

        String query = String.format("""
                SELECT *
                FROM refined_tickit_public_venue
                %s
                ORDER BY venueid
                OFFSET %s
                LIMIT %s;""", whereClause, offset, limit);

        return startQuery(query);
    }

    public Venue findById(int id) {
        String query = String.format("""
                SELECT  DISTINCT *
                FROM refined_tickit_public_venue
                WHERE venueid=%s""", id);

        return startQuery(query).get(0);
    }

    private List<Venue> startQuery(String query) {

        logger.debug(String.format("Query: %s", query));

        AthenaClient athenaClient = athenaClientFactory.createClient(
                configProperties.getRegion(),
                configProperties.getIamProfile()
        );
        String queryExecutionId = athenaCommon.submitAthenaQuery(athenaClient, query);
        athenaCommon.waitForQueryToComplete(athenaClient, queryExecutionId);
        List<Venue> venues = processResultRows(athenaClient, queryExecutionId);
        athenaClient.close();

        return venues;
    }

    private List<Venue> processResultRows(AthenaClient athenaClient, String queryExecutionId) {

        List<Venue> venues = new ArrayList<>();

        try {
            // Max Results can be set but if it's not set,
            // it will choose the maximum page size
            GetQueryResultsRequest getQueryResultsRequest = GetQueryResultsRequest.builder()
                    .queryExecutionId(queryExecutionId).build();

            GetQueryResultsIterable getQueryResultsResults = athenaClient.getQueryResultsPaginator(getQueryResultsRequest);

            List<Row> rows;

            for (GetQueryResultsResponse result : getQueryResultsResults) {
                rows = result.resultSet().rows();

                for (Row myRow : rows.subList(1, rows.size())) { // skip first row - column names
                    List<Datum> allData = myRow.data();
                    Venue venue = new Venue();
                    venue.setId(parseInt(allData.get(0).varCharValue()));
                    venue.setName(allData.get(1).varCharValue());
                    venue.setCity(allData.get(2).varCharValue());
                    venue.setState(allData.get(3).varCharValue());
                    venue.setSeats(allData.get(4).varCharValue() == null ? null : parseInt(allData.get(4).varCharValue()));
                    venues.add(venue);
                }
            }
        } catch (AthenaException e) {
            logger.error(e.getMessage());
        }
        return venues;
    }
}
