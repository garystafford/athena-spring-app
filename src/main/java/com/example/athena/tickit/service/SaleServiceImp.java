package com.example.athena.tickit.service;

import com.example.athena.common.AthenaClientFactory;
import com.example.athena.common.AthenaCommon;
import com.example.athena.config.ConfigProperties;
import com.example.athena.tickit.model.ecomm.Sale;
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
public class SaleServiceImp implements SaleService {

    private static final Logger logger = LoggerFactory.getLogger(SaleServiceImp.class);

    private final ConfigProperties configProperties;

    private final AthenaClientFactory athenaClientFactory;

    private final AthenaCommon athenaCommon;

    @Autowired
    public SaleServiceImp(ConfigProperties configProperties, AthenaClientFactory athenaClientFactory, AthenaCommon athenaCommon) {
        this.configProperties = configProperties;
        this.athenaClientFactory = athenaClientFactory;
        this.athenaCommon = athenaCommon;
    }

    public List<Sale> findAll(Integer limit, Integer offset) {

        if (limit == null || limit < 1 || limit > configProperties.getLimit()) {
            limit = configProperties.getLimit();
        }

        if (offset == null || offset < 1) {
            offset = 0;
        }

        String whereClause = "WHERE salesid IS NOT NULL";

        String query = String.format("""
                SELECT *
                FROM refined_tickit_public_sales
                %s
                ORDER BY salesid
                OFFSET %s
                LIMIT %s;""", whereClause, offset, limit);

        return startQuery(query);
    }

    public Sale findById(int id) {
        String query = String.format("""
                SELECT  DISTINCT *
                FROM refined_tickit_public_sales
                WHERE salesid=%s""", id);

        Sale sale;
        try {
            sale = startQuery(query).get(0);
        } catch (IndexOutOfBoundsException e) {
            logger.error(e.getMessage());
            return null;
        }
        return sale;
    }

    private List<Sale> startQuery(String query) {

        logger.debug(String.format("Query: %s", query.replace("\n", " ")));

        AthenaClient athenaClient = athenaClientFactory.createClient();
        String queryExecutionId = athenaCommon.submitAthenaQuery(athenaClient, query);
        athenaCommon.waitForQueryToComplete(athenaClient, queryExecutionId);
        List<Sale> sales = processResultRows(athenaClient, queryExecutionId);
        athenaClient.close();

        return sales;
    }

    private List<Sale> processResultRows(AthenaClient athenaClient, String queryExecutionId) {

        List<Sale> sales = new ArrayList<>();

        try {
            // Max Results can be set but if it's not set,
            // it will choose the maximum page size
            GetQueryResultsRequest getQueryResultsRequest = GetQueryResultsRequest.builder()
                    .queryExecutionId(queryExecutionId).build();

            GetQueryResultsIterable getQueryResultsResults = athenaClient.getQueryResultsPaginator(getQueryResultsRequest);

            List<Row> rows;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy HH:mm:ss");

            for (GetQueryResultsResponse result : getQueryResultsResults) {
                rows = result.resultSet().rows();

                for (Row myRow : rows.subList(1, rows.size())) { // skip first row - column names
                    List<Datum> allData = myRow.data();
                    Sale sale = new Sale();
                    sale.setId(parseInt(allData.get(0).varCharValue()));
                    sale.setListId(parseInt(allData.get(1).varCharValue()));
                    sale.setBuyerId(parseInt(allData.get(2).varCharValue()));
                    sale.setSellerId(parseInt(allData.get(3).varCharValue()));
                    sale.setEventId(parseInt(allData.get(4).varCharValue()));
                    sale.setQtySold(parseInt(allData.get(5).varCharValue()));
                    sale.setId(parseInt(allData.get(6).varCharValue()));
                    sale.setPricePaid(new BigDecimal(allData.get(7).varCharValue()));
                    sale.setCommission(new BigDecimal(allData.get(8).varCharValue()));
                    sale.setSaleTime(LocalDateTime.parse(allData.get(9).varCharValue(), formatter));
                    sales.add(sale);
                }
            }
        } catch (AthenaException e) {
            logger.error(e.getMessage());
        }
        return sales;
    }
}
