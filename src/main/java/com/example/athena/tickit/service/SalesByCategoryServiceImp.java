package com.example.athena.tickit.service;

import com.example.athena.common.AthenaClientFactory;
import com.example.athena.common.AthenaCommon;
import com.example.athena.config.ConfigProperties;
import com.example.athena.tickit.model.resultsets.SalesByCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.*;
import software.amazon.awssdk.services.athena.paginators.GetQueryResultsIterable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SalesByCategoryServiceImp implements SalesByCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(SalesByCategoryServiceImp.class);

    private final ConfigProperties configProperties;

    private final AthenaClientFactory athenaClientFactory;

    private final AthenaCommon athenaCommon;

    @Autowired
    public SalesByCategoryServiceImp(ConfigProperties configProperties, AthenaClientFactory athenaClientFactory, AthenaCommon athenaCommon) {
        this.configProperties = configProperties;
        this.athenaClientFactory = athenaClientFactory;
        this.athenaCommon = athenaCommon;
    }

    public List<SalesByCategory> findAll(String calendarDate, Integer limit, Integer offset) {

        if (limit == null || limit < 1 || limit > configProperties.getLimit()) {
            limit = configProperties.getLimit();
        }

        if (offset == null || offset < 1) {
            offset = 0;
        }

        String whereClause = "WHERE caldate IS NOT NULL";
        if (calendarDate != null) {
            whereClause = whereClause + " AND caldate=date('" + calendarDate + "')";
        }

        String query = String.format("""
                SELECT *
                FROM tickit_sales_by_day_and_category
                %s
                OFFSET %s
                LIMIT %s;""", whereClause, offset, limit);

        return startQuery(query);
    }

    private List<SalesByCategory> startQuery(String query) {

        logger.debug(String.format("Query: %s", query));

        AthenaClient athenaClient = athenaClientFactory.createClient(configProperties.getRegion(), configProperties.getIamProfile());
        String queryExecutionId = athenaCommon.submitAthenaQuery(athenaClient, query);
        athenaCommon.waitForQueryToComplete(athenaClient, queryExecutionId);
        List<SalesByCategory> categories = processResultRows(athenaClient, queryExecutionId);
        athenaClient.close();

        return categories;
    }

    private List<SalesByCategory> processResultRows(AthenaClient athenaClient, String queryExecutionId) {

        List<SalesByCategory> salesByCategories = new ArrayList<>();

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
                    SalesByCategory salesByCategory = new SalesByCategory();
                    salesByCategory.setCalendarDate(LocalDate.parse(allData.get(0).varCharValue()));
                    salesByCategory.setCategoryGroup(allData.get(1).varCharValue());
                    salesByCategory.setCategoryName(allData.get(2).varCharValue());
                    salesByCategory.setSaleAmount(new BigDecimal(allData.get(3).varCharValue()));
                    salesByCategory.setCommission(new BigDecimal(allData.get(4).varCharValue()));
                    salesByCategories.add(salesByCategory);
                }
            }
        } catch (AthenaException e) {
            logger.error(e.getMessage());
        }
        return salesByCategories;
    }
}
