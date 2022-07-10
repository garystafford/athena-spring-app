package com.example.athena.tickit.service;

import com.example.athena.common.AthenaClientFactory;
import com.example.athena.common.AthenaCommon;
import com.example.athena.config.ConfigProperties;
import com.example.athena.tickit.model.resultsets.BuyerLikesByCategory;
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
public class BuyerLikesByCategoryServiceImp implements BuyersLikesByCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(BuyerLikesByCategoryServiceImp.class);

    private final ConfigProperties configProperties;

    private final AthenaClientFactory athenaClientFactory;

    private final AthenaCommon athenaCommon;

    @Autowired
    public BuyerLikesByCategoryServiceImp(ConfigProperties configProperties, AthenaClientFactory athenaClientFactory, AthenaCommon athenaCommon) {
        this.configProperties = configProperties;
        this.athenaClientFactory = athenaClientFactory;
        this.athenaCommon = athenaCommon;
    }

    public List<BuyerLikesByCategory> get() {
        return getNamedQueryResults(configProperties.getNamedQueryId());
    }

    private List<BuyerLikesByCategory> getNamedQueryResults(String queryId) {

        logger.debug(String.format("NamedQueryId: %s", queryId));

        AthenaClient athenaClient = athenaClientFactory.createClient();
        GetNamedQueryRequest getNamedQueryRequest = GetNamedQueryRequest.builder()
                .namedQueryId(queryId)
                .build();
        GetNamedQueryResponse getNamedQueryResponse = athenaClient.getNamedQuery(getNamedQueryRequest);
        String queryExecutionId = athenaCommon.submitAthenaQuery(athenaClient, getNamedQueryResponse.namedQuery().queryString());
        athenaCommon.waitForQueryToComplete(athenaClient, queryExecutionId);
        List<BuyerLikesByCategory> buyerLikesByCategories = processResultRows(athenaClient, queryExecutionId);
        athenaClient.close();

        return buyerLikesByCategories;
    }

    private List<BuyerLikesByCategory> processResultRows(AthenaClient athenaClient, String queryExecutionId) {

        List<BuyerLikesByCategory> buyerLikesByCategories = new ArrayList<>();

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
                    BuyerLikesByCategory buyerLikesByCategory = new BuyerLikesByCategory();
                    buyerLikesByCategory.setSports(parseInt(allData.get(0).varCharValue()));
                    buyerLikesByCategory.setTheatre(parseInt(allData.get(1).varCharValue()));
                    buyerLikesByCategory.setConcerts(parseInt(allData.get(2).varCharValue()));
                    buyerLikesByCategory.setJazz(parseInt(allData.get(3).varCharValue()));
                    buyerLikesByCategory.setClassical(parseInt(allData.get(4).varCharValue()));
                    buyerLikesByCategory.setOpera(parseInt(allData.get(5).varCharValue()));
                    buyerLikesByCategory.setRock(parseInt(allData.get(6).varCharValue()));
                    buyerLikesByCategory.setVegas(parseInt(allData.get(7).varCharValue()));
                    buyerLikesByCategory.setBroadway(parseInt(allData.get(8).varCharValue()));
                    buyerLikesByCategory.setMusicals(parseInt(allData.get(9).varCharValue()));
                    buyerLikesByCategories.add(buyerLikesByCategory);
                }
            }
        } catch (AthenaException e) {
            logger.error(e.getMessage());
        }
        return buyerLikesByCategories;
    }
}
