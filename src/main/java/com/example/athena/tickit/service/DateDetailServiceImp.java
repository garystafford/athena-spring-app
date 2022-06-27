package com.example.athena.tickit.service;

import com.example.athena.common.AthenaClientFactory;
import com.example.athena.common.AthenaCommon;
import com.example.athena.config.ConfigProperties;
import com.example.athena.tickit.model.ecomm.DateDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.*;
import software.amazon.awssdk.services.athena.paginators.GetQueryResultsIterable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

@Service
public class DateDetailServiceImp implements DateDetailService {

    private static final Logger logger = LoggerFactory.getLogger(DateDetailServiceImp.class);

    private final ConfigProperties configProperties;

    private final AthenaClientFactory athenaClientFactory;

    private final AthenaCommon athenaCommon;

    @Autowired
    public DateDetailServiceImp(ConfigProperties configProperties, AthenaClientFactory athenaClientFactory, AthenaCommon athenaCommon) {
        this.configProperties = configProperties;
        this.athenaClientFactory = athenaClientFactory;
        this.athenaCommon = athenaCommon;
    }

    public List<DateDetail> findAll(Integer limit, Integer offset) {

        if (limit == null || limit < 1 || limit > configProperties.getLimit()) {
            limit = configProperties.getLimit();
        }

        if (offset == null || offset < 1) {
            offset = 0;
        }

        String whereClause = "WHERE dateid IS NOT NULL";

        String query = String.format("""
                SELECT *
                FROM refined_tickit_public_date
                %s
                ORDER BY dateid
                OFFSET %s
                LIMIT %s;""", whereClause, offset, limit);

        return startQuery(query);
    }

    public DateDetail findById(int id) {
        String query = String.format("""
                SELECT  DISTINCT *
                FROM refined_tickit_public_date
                WHERE dateid=%s""", id);

        return startQuery(query).get(0);
    }

    private List<DateDetail> startQuery(String query) {

        logger.debug(String.format("Query: %s", query));

        AthenaClient athenaClient = athenaClientFactory.createClient();
        String queryExecutionId = athenaCommon.submitAthenaQuery(athenaClient, query);
        athenaCommon.waitForQueryToComplete(athenaClient, queryExecutionId);
        List<DateDetail> dateDetails = processResultRows(athenaClient, queryExecutionId);
        athenaClient.close();

        return dateDetails;
    }

    private List<DateDetail> processResultRows(AthenaClient athenaClient, String queryExecutionId) {

        List<DateDetail> categories = new ArrayList<>();

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
                    DateDetail dateDetail = new DateDetail();
                    dateDetail.setId(parseInt(allData.get(0).varCharValue()));
                    dateDetail.setCalendarDate(LocalDate.parse(allData.get(1).varCharValue()));
                    dateDetail.setDay(allData.get(2).varCharValue());
                    dateDetail.setWeek(allData.get(3).varCharValue());
                    dateDetail.setMonth(allData.get(4).varCharValue());
                    dateDetail.setQuarter(allData.get(5).varCharValue());
                    dateDetail.setYear(Integer.parseInt(allData.get(6).varCharValue()));
                    dateDetail.setHoliday(Boolean.parseBoolean(allData.get(7).varCharValue()));
                    categories.add(dateDetail);
                }
            }
        } catch (AthenaException e) {
            logger.error(e.getMessage());
        }
        return categories;
    }
}
