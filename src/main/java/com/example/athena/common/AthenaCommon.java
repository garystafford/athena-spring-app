package com.example.athena.common;

import com.example.athena.config.ConfigProperties;
import com.example.athena.moma.service.ArtistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.*;

@Component
public class AthenaCommon {

    private static final Logger logger = LoggerFactory.getLogger(ArtistService.class);

    private final ConfigProperties configProperties;

    @Autowired
    public AthenaCommon(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    public String submitAthenaQuery(AthenaClient athenaClient, String query) {

        try {

            // The QueryExecutionContext allows us to set the database
            QueryExecutionContext queryExecutionContext = QueryExecutionContext.builder()
                    .database(configProperties.getDatabase())
                    .build();

            // The result configuration specifies where the results of the query should go
            ResultConfiguration resultConfiguration = ResultConfiguration.builder()
                    .outputLocation(configProperties.getResultsBucket())
                    .build();

            StartQueryExecutionRequest startQueryExecutionRequest = StartQueryExecutionRequest.builder()
                    .queryString(query)
                    .queryExecutionContext(queryExecutionContext)
                    .resultConfiguration(resultConfiguration)
                    .build();

            StartQueryExecutionResponse startQueryExecutionResponse = athenaClient.startQueryExecution(startQueryExecutionRequest);
            return startQueryExecutionResponse.queryExecutionId();

        } catch (AthenaException e) {
            logger.error(e.getMessage());
        }
        return "";
    }

    // Wait for an Amazon Athena query to complete, fail or to be cancelled
    public void waitForQueryToComplete(AthenaClient athenaClient, String queryExecutionId) {

        GetQueryExecutionRequest getQueryExecutionRequest = GetQueryExecutionRequest.builder()
                .queryExecutionId(queryExecutionId).build();

        GetQueryExecutionResponse getQueryExecutionResponse;
        boolean isQueryStillRunning = true;
        while (isQueryStillRunning) {
            getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest);
            String queryState = getQueryExecutionResponse.queryExecution().status().state().toString();
            if (queryState.equals(QueryExecutionState.FAILED.toString())) {
                throw new RuntimeException("The Amazon Athena query failed to run with error message: " + getQueryExecutionResponse
                        .queryExecution().status().stateChangeReason());
            } else if (queryState.equals(QueryExecutionState.CANCELLED.toString())) {
                throw new RuntimeException("The Amazon Athena query was cancelled.");
            } else if (queryState.equals(QueryExecutionState.SUCCEEDED.toString())) {
                isQueryStillRunning = false;
            } else {
                // Sleep an amount of time before retrying again
                try {
                    Thread.sleep(configProperties.getRetrySleep());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            logger.debug("The current status is: " + queryState);
        }
    }
}
