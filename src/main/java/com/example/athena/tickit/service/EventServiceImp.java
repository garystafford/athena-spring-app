package com.example.athena.tickit.service;

import com.example.athena.common.AthenaClientFactory;
import com.example.athena.common.AthenaCommon;
import com.example.athena.config.ConfigProperties;
import com.example.athena.tickit.model.saas.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.*;
import software.amazon.awssdk.services.athena.paginators.GetQueryResultsIterable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

@Service
public class EventServiceImp implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImp.class);

    private final ConfigProperties configProperties;

    private final AthenaClientFactory athenaClientFactory;

    private final AthenaCommon athenaCommon;

    @Autowired
    public EventServiceImp(ConfigProperties configProperties, AthenaClientFactory athenaClientFactory, AthenaCommon athenaCommon) {
        this.configProperties = configProperties;
        this.athenaClientFactory = athenaClientFactory;
        this.athenaCommon = athenaCommon;
    }

    public List<Event> findAll(Integer limit, Integer offset) {

        if (limit == null || limit < 1 || limit > configProperties.getLimit()) {
            limit = configProperties.getLimit();
        }

        if (offset == null || offset < 1) {
            offset = 0;
        }

        String whereClause = "WHERE eventid IS NOT NULL";

        String query = String.format("""
                SELECT *
                FROM refined_tickit_public_event
                %s
                ORDER BY eventid
                OFFSET %s
                LIMIT %s;""", whereClause, offset, limit);

        return startQuery(query);
    }

    public Event findById(int id) {
        String query = String.format("""
                SELECT  DISTINCT *
                FROM refined_tickit_public_event
                WHERE eventid=%s""", id);

        return startQuery(query).get(0);
    }

    private List<Event> startQuery(String query) {

        logger.debug(String.format("Query: %s", query));

        AthenaClient athenaClient = athenaClientFactory.createClient(configProperties.getRegion(), configProperties.getIamProfile());
        String queryExecutionId = athenaCommon.submitAthenaQuery(athenaClient, query);
        athenaCommon.waitForQueryToComplete(athenaClient, queryExecutionId);
        List<Event> events = processResultRows(athenaClient, queryExecutionId);
        athenaClient.close();

        return events;
    }

    private List<Event> processResultRows(AthenaClient athenaClient, String queryExecutionId) {

        List<Event> events = new ArrayList<>();

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
                    Event event = new Event();
                    event.setId(parseInt(allData.get(0).varCharValue()));
                    event.setVenueId(parseInt(allData.get(1).varCharValue()));
                    event.setCatId(parseInt(allData.get(2).varCharValue()));
                    event.setDateId(parseInt(allData.get(3).varCharValue()));
                    event.setName(allData.get(4).varCharValue());
                    event.setStartTime(LocalDateTime.parse(allData.get(5).varCharValue(), formatter));
                    events.add(event);
                }
            }
        } catch (AthenaException e) {
            logger.error(e.getMessage());
        }
        return events;
    }
}
