package com.example.athena.common;

import com.example.athena.config.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.GetTableMetadataRequest;
import software.amazon.awssdk.services.athena.model.GetTableMetadataResponse;
import software.amazon.awssdk.services.athena.model.MetadataException;

@Component
public class View {

    private static final Logger logger = LoggerFactory.getLogger(View.class);

    private final AthenaClientFactory athenaClientFactoryImp;

    private final ConfigProperties configProperties;

    private final AthenaCommon athenaCommon;

    @Autowired
    public View(AthenaClientFactory athenaClientFactoryImp, ConfigProperties configProperties, AthenaCommon athenaCommon) {
        this.athenaClientFactoryImp = athenaClientFactoryImp;
        this.configProperties = configProperties;
        this.athenaCommon = athenaCommon;
    }

    public void CreateView() {
        String viewName = "view_tickit_sales_by_day_and_category";

        String createViewSqlStatement = String.format("""
                CREATE VIEW %s AS
                SELECT cast(d.caldate AS DATE) AS caldate,
                    c.catgroup,
                    c.catname,
                     sum(round(cast(s.pricepaid AS DECIMAL(8,2)) * s.qtysold, 2)) AS saleamount,
                    sum(cast(s.commission AS DECIMAL(8,2))) AS commission
                 FROM refined_tickit_public_sales AS s
                    LEFT JOIN refined_tickit_public_event AS e ON e.eventid = s.eventid
                    LEFT JOIN refined_tickit_public_date AS d ON d.dateid = s.dateid
                    LEFT JOIN refined_tickit_public_category AS c ON c.catid = e.catid
                 GROUP BY caldate,
                    catgroup,
                    catname
                 ORDER BY caldate,
                    catgroup,
                    catname;""", viewName);

        try (AthenaClient athenaClient = athenaClientFactoryImp.createClient()) {
            try {
                GetTableMetadataResponse getPreparedStatementRequest = getGetTableMetadataResponse(viewName, athenaClient);
                logger.debug(String.format("View already exists: %s", getPreparedStatementRequest.tableMetadata().name()));
            } catch (MetadataException e) { // View does not exist
                String queryExecutionId = athenaCommon.submitAthenaQuery(athenaClient, createViewSqlStatement);
                athenaCommon.waitForQueryToComplete(athenaClient, queryExecutionId);

                // Confirm View was created
                GetTableMetadataResponse getPreparedStatementRequest = getGetTableMetadataResponse(viewName, athenaClient);
                logger.debug(String.format("View created successfully: %s", getPreparedStatementRequest.tableMetadata().name()));
            }
        }
    }

    private GetTableMetadataResponse getGetTableMetadataResponse(String viewName, AthenaClient athenaClient) {
        GetTableMetadataRequest getTableMetadataRequest = GetTableMetadataRequest.builder()
                .catalogName(configProperties.getCatalog())
                .databaseName(configProperties.getDatabase())
                .tableName(viewName)
                .build();

        return athenaClient.getTableMetadata(getTableMetadataRequest);
    }
}
