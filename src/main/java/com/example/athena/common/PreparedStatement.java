package com.example.athena.common;

import com.example.athena.config.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.CreatePreparedStatementRequest;
import software.amazon.awssdk.services.athena.model.GetPreparedStatementRequest;
import software.amazon.awssdk.services.athena.model.GetPreparedStatementResponse;
import software.amazon.awssdk.services.athena.model.ResourceNotFoundException;

@Component
public class PreparedStatement {

    private static final Logger logger = LoggerFactory.getLogger(PreparedStatement.class);

    private final AthenaClientFactory athenaClientFactoryImp;

    private final ConfigProperties configProperties;

    @Autowired
    public PreparedStatement(AthenaClientFactory athenaClientFactoryImp, ConfigProperties configProperties) {
        this.athenaClientFactoryImp = athenaClientFactoryImp;
        this.configProperties = configProperties;
    }

    public void CreatePreparedStatement() {
        String preparedStatementName = "tickit_sales_by_seller";

        String preparedStatementSql = """
                SELECT cast(d.caldate AS DATE) AS caldate,
                    s.pricepaid,
                    s.qtysold,
                    round(cast(s.pricepaid AS DECIMAL(8,2)) * s.qtysold, 2) AS saleamount,
                    cast(s.commission AS DECIMAL(8,2)) AS commission,
                    round((cast(s.commission AS DECIMAL(8,2)) / (cast(s.pricepaid AS DECIMAL(8,2)) * s.qtysold)) * 100, 2) AS commissionprcnt,
                    e.eventname,
                    concat(u1.firstname, ' ', u1.lastname) AS seller,
                    concat(u2.firstname, ' ', u2.lastname) AS buyer,
                    c.catgroup,
                    c.catname
                FROM refined_tickit_public_sales AS s
                    LEFT JOIN refined_tickit_public_listing AS l ON l.listid = s.listid
                    LEFT JOIN refined_tickit_public_users AS u1 ON u1.userid = s.sellerid
                    LEFT JOIN refined_tickit_public_users AS u2 ON u2.userid = s.buyerid
                    LEFT JOIN refined_tickit_public_event AS e ON e.eventid = s.eventid
                    LEFT JOIN refined_tickit_public_date AS d ON d.dateid = s.dateid
                    LEFT JOIN refined_tickit_public_category AS c ON c.catid = e.catid
                WHERE s.sellerid = ?
                ORDER BY caldate,
                    eventname;""";

        try (AthenaClient athenaClient = athenaClientFactoryImp.createClient(configProperties.getRegion(), configProperties.getIamProfile())) {
            try {
                GetPreparedStatementResponse getPreparedStatementResponse = getGetPreparedStatementResponse(preparedStatementName, athenaClient);
                logger.debug(String.format("Prepared statement already exists: %s", getPreparedStatementResponse.preparedStatement().statementName()));
            } catch (ResourceNotFoundException e) { // PreparedStatement does not exist
                CreatePreparedStatementRequest createPreparedStatementRequest = CreatePreparedStatementRequest.builder()
                        .statementName(preparedStatementName)
                        .description("Returns all sales by seller based on the seller's userid")
                        .workGroup(configProperties.getWorkGroup())
                        .queryStatement(preparedStatementSql).build();
                athenaClient.createPreparedStatement(createPreparedStatementRequest);

                // Confirm PreparedStatement was created
                GetPreparedStatementResponse getPreparedStatementResponse = getGetPreparedStatementResponse(preparedStatementName, athenaClient);
                logger.debug(String.format("Prepared statement created successfully: %s", getPreparedStatementResponse.preparedStatement().statementName()));
            }
        }
    }

    private GetPreparedStatementResponse getGetPreparedStatementResponse(String preparedStatementName, AthenaClient athenaClient) {
        GetPreparedStatementRequest getPreparedStatementRequest = GetPreparedStatementRequest.builder()
                .statementName(preparedStatementName)
                .workGroup(configProperties.getWorkGroup()).build();

        return athenaClient.getPreparedStatement(getPreparedStatementRequest);
    }
}
