package com.example.athena.tickit.service;

import com.example.athena.common.AthenaClientFactory;
import com.example.athena.common.AthenaCommon;
import com.example.athena.config.ConfigProperties;
import com.example.athena.tickit.model.crm.User;
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
public class UserServiceImp implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

    private final ConfigProperties configProperties;

    private final AthenaClientFactory athenaClientFactory;

    private final AthenaCommon athenaCommon;

    @Autowired
    public UserServiceImp(ConfigProperties configProperties, AthenaClientFactory athenaClientFactory, AthenaCommon athenaCommon) {
        this.configProperties = configProperties;
        this.athenaClientFactory = athenaClientFactory;
        this.athenaCommon = athenaCommon;
    }

    public List<User> findAll(Integer limit, Integer offset) {

        if (limit == null || limit < 1 || limit > configProperties.getLimit()) {
            limit = configProperties.getLimit();
        }

        if (offset == null || offset < 1) {
            offset = 0;
        }

        String whereClause = "WHERE userid IS NOT NULL";

        String query = String.format("""
                SELECT *
                FROM refined_tickit_public_users
                %s
                ORDER BY userid
                OFFSET %s
                LIMIT %s;""", whereClause, offset, limit);

        return startQuery(query);
    }

    public User findById(int id) {
        String query = String.format("""
                SELECT  DISTINCT *
                FROM refined_tickit_public_users
                WHERE userid=%s""", id);

        User user;
        try {
            user = startQuery(query).get(0);
        } catch (IndexOutOfBoundsException e) {
            logger.error(e.getMessage());
            return null;
        }
        return user;
    }

    private List<User> startQuery(String query) {

        logger.debug(String.format("Query: %s", query.replace("\n", " ")));

        AthenaClient athenaClient = athenaClientFactory.createClient();
        String queryExecutionId = athenaCommon.submitAthenaQuery(athenaClient, query);
        athenaCommon.waitForQueryToComplete(athenaClient, queryExecutionId);
        List<User> users = processResultRows(athenaClient, queryExecutionId);
        athenaClient.close();

        return users;
    }

    private List<User> processResultRows(AthenaClient athenaClient, String queryExecutionId) {

        List<User> users = new ArrayList<>();

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
                    User user = new User();
                    user.setId(parseInt(allData.get(0).varCharValue()));
                    user.setUsername(allData.get(1).varCharValue());
                    user.setFirstName(allData.get(2).varCharValue());
                    user.setLastName(allData.get(3).varCharValue());
                    user.setCity(allData.get(4).varCharValue());
                    user.setState(allData.get(5).varCharValue());
                    user.setEmail(allData.get(6).varCharValue());
                    user.setPhone(allData.get(7).varCharValue());
                    user.setLikeSports(Boolean.valueOf(allData.get(8).varCharValue()));
                    user.setLikeTheatre(Boolean.valueOf(allData.get(9).varCharValue()));
                    user.setLikeConcerts(Boolean.valueOf(allData.get(10).varCharValue()));
                    user.setLikeJazz(Boolean.valueOf(allData.get(11).varCharValue()));
                    user.setLikeClassical(Boolean.valueOf(allData.get(12).varCharValue()));
                    user.setLikeOpera(Boolean.valueOf(allData.get(13).varCharValue()));
                    user.setLikeRock(Boolean.valueOf(allData.get(14).varCharValue()));
                    user.setLikeVegas(Boolean.valueOf(allData.get(15).varCharValue()));
                    user.setLikeBroadway(Boolean.valueOf(allData.get(16).varCharValue()));
                    user.setLikeMusicals(Boolean.valueOf(allData.get(17).varCharValue()));
                    users.add(user);
                }
            }
        } catch (AthenaException e) {
            logger.error(e.getMessage());
        }
        return users;
    }
}
