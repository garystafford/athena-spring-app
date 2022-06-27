package com.example.athena.tickit.service;

import com.example.athena.common.AthenaClientFactory;
import com.example.athena.common.AthenaCommon;
import com.example.athena.config.ConfigProperties;
import com.example.athena.tickit.model.resultsets.SaleBySeller;
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
public class SaleBySellerServiceImp implements SaleBySellerService {

    private static final Logger logger = LoggerFactory.getLogger(SaleBySellerServiceImp.class);

    private final ConfigProperties configProperties;

    private final AthenaClientFactory athenaClientFactory;

    private final AthenaCommon athenaCommon;

    @Autowired
    public SaleBySellerServiceImp(ConfigProperties configProperties, AthenaClientFactory athenaClientFactory, AthenaCommon athenaCommon) {
        this.configProperties = configProperties;
        this.athenaClientFactory = athenaClientFactory;
        this.athenaCommon = athenaCommon;
    }

    public List<SaleBySeller> find(int id) {

        String query = String.format("""
                EXECUTE tickit_sales_by_seller USING %s;""", id);

        return startQuery(query);
    }

    private List<SaleBySeller> startQuery(String query) {

        logger.debug(String.format("Query: %s", query));

        AthenaClient athenaClient = athenaClientFactory.createClient();
        String queryExecutionId = athenaCommon.submitAthenaQuery(athenaClient, query);
        athenaCommon.waitForQueryToComplete(athenaClient, queryExecutionId);
        List<SaleBySeller> saleBySellers = processResultRows(athenaClient, queryExecutionId);
        athenaClient.close();

        return saleBySellers;
    }

    private List<SaleBySeller> processResultRows(AthenaClient athenaClient, String queryExecutionId) {

        List<SaleBySeller> saleBySellers = new ArrayList<>();

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
                    SaleBySeller saleBySeller = new SaleBySeller();
                    saleBySeller.setCalDate(LocalDate.parse(allData.get(0).varCharValue()));
                    saleBySeller.setPricePaid(new BigDecimal(allData.get(1).varCharValue()));
                    saleBySeller.setQtySold(Integer.parseInt(allData.get(2).varCharValue()));
                    saleBySeller.setSaleAmount(new BigDecimal(allData.get(3).varCharValue()));
                    saleBySeller.setCommission(new BigDecimal(allData.get(4).varCharValue()));
                    saleBySeller.setCommissionPrcnt(Double.valueOf(allData.get(5).varCharValue()));
                    saleBySeller.setEventName(allData.get(6).varCharValue());
                    saleBySeller.setSeller(allData.get(7).varCharValue());
                    saleBySeller.setBuyer(allData.get(8).varCharValue());
                    saleBySeller.setCatGroup(allData.get(9).varCharValue());
                    saleBySeller.setCatName(allData.get(10).varCharValue());
                    saleBySellers.add(saleBySeller);
                }
            }
        } catch (AthenaException e) {
            logger.error(e.getMessage());
        }
        return saleBySellers;
    }
}
