package com.example.athena.tickit.service;

import com.example.athena.common.AthenaClientFactory;
import com.example.athena.common.AthenaCommon;
import com.example.athena.config.ConfigProperties;
import com.example.athena.tickit.model.saas.Category;
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
public class CategoryServiceImp implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImp.class);

    private final ConfigProperties configProperties;

    private final AthenaClientFactory athenaClientFactory;

    private final AthenaCommon athenaCommon;

    @Autowired
    public CategoryServiceImp(ConfigProperties configProperties, AthenaClientFactory athenaClientFactory, AthenaCommon athenaCommon) {
        this.configProperties = configProperties;
        this.athenaClientFactory = athenaClientFactory;
        this.athenaCommon = athenaCommon;
    }

    public List<Category> findAll(Integer limit, Integer offset) {

        if (limit == null || limit < 1 || limit > configProperties.getLimit()) {
            limit = configProperties.getLimit();
        }

        if (offset == null || offset < 1) {
            offset = 0;
        }

        String whereClause = "WHERE catid IS NOT NULL";

        String query = String.format("""
                SELECT *
                FROM refined_tickit_public_category
                %s
                ORDER BY catid
                OFFSET %s
                LIMIT %s;""", whereClause, offset, limit);

        return startQuery(query);
    }

    public Category findById(int id) {
        String query = String.format("""
                SELECT  DISTINCT *
                FROM refined_tickit_public_category
                WHERE catid=%s;""", id);

        Category category;
        try {
            category = startQuery(query).get(0);
        } catch (IndexOutOfBoundsException e) {
            logger.error(e.getMessage());
            return null;
        }
        return category;
    }

    private List<Category> startQuery(String query) {

        logger.debug(String.format("Query: %s", query.replace("\n", " ")));

        AthenaClient athenaClient = athenaClientFactory.createClient();
        String queryExecutionId = athenaCommon.submitAthenaQuery(athenaClient, query);
        athenaCommon.waitForQueryToComplete(athenaClient, queryExecutionId);
        List<Category> categories = processResultRows(athenaClient, queryExecutionId);
        athenaClient.close();

        return categories;
    }

    private List<Category> processResultRows(AthenaClient athenaClient, String queryExecutionId) {

        List<Category> categories = new ArrayList<>();

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
                    Category category = new Category();
                    category.setId(parseInt(allData.get(0).varCharValue()));
                    category.setGroup(allData.get(1).varCharValue());
                    category.setName(allData.get(2).varCharValue());
                    category.setDescription(allData.get(3).varCharValue());
                    categories.add(category);
                }
            }
        } catch (AthenaException e) {
            logger.error(e.getMessage());
        }
        return categories;
    }
}
