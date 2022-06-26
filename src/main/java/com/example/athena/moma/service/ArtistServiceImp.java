package com.example.athena.moma.service;

import com.example.athena.common.AthenaClientFactory;
import com.example.athena.common.AthenaCommon;
import com.example.athena.config.ConfigProperties;
import com.example.athena.moma.model.Artist;
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
public class ArtistServiceImp implements ArtistService {

    private static final Logger logger = LoggerFactory.getLogger(ArtistService.class);

    private final ConfigProperties configProperties;

    private final AthenaClientFactory athenaClientFactoryImp;

    private final AthenaCommon athenaCommon;

    @Autowired
    public ArtistServiceImp(ConfigProperties configProperties, AthenaClientFactory athenaClientFactoryImp, AthenaCommon athenaCommon) {
        this.configProperties = configProperties;
        this.athenaClientFactoryImp = athenaClientFactoryImp;
        this.athenaCommon = athenaCommon;
    }

    public List<Artist> findAll(String nationality, Integer limit, Integer offset) {

        if (limit == null || limit < 1 || limit > configProperties.getLimit()) {
            limit = configProperties.getLimit();
        }

        if (offset == null || offset < 1) {
            offset = 0;
        }

        String whereClause = "WHERE artist_id IS NOT NULL";
        if (nationality != null) {
            whereClause = whereClause + " AND nationality='" + nationality + "'";
        }

        String query = String.format("""
                SELECT *
                FROM artists_refined_glue
                %s
                ORDER BY artist_id
                OFFSET %s
                LIMIT %s;""", whereClause, offset, limit);

        return startQuery(query);
    }

    public Artist findById(int id) {
        String query = String.format("""
                SELECT  DISTINCT *
                FROM artists_refined_glue
                WHERE artist_id=%s""", id);

        return startQuery(query).get(0);
    }

    // Submits a sample query to Amazon Athena and returns the execution ID of the query
    private List<Artist> startQuery(String query) {

        logger.debug(String.format("Query: %s", query));

        AthenaClient athenaClient = athenaClientFactoryImp.createClient(configProperties.getRegion(), configProperties.getIamProfile());
        String queryExecutionId = athenaCommon.submitAthenaQuery(athenaClient, query);
        athenaCommon.waitForQueryToComplete(athenaClient, queryExecutionId);
        List<Artist> artists = processResultRows(athenaClient, queryExecutionId);
        athenaClient.close();

        return artists;
    }

    private List<Artist> processResultRows(AthenaClient athenaClient, String queryExecutionId) {

        List<Artist> artists = new ArrayList<>();

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
                    Artist artist = new Artist();
                    artist.setId(parseInt(allData.get(0).varCharValue()));
                    artist.setName(allData.get(1).varCharValue());
                    artist.setGender(allData.get(2).varCharValue());
                    artist.setBirthYear(allData.get(3).varCharValue() == null ? null : parseInt(allData.get(3).varCharValue()));
                    artist.setDeathYear(allData.get(4).varCharValue() == null ? null : parseInt(allData.get(4).varCharValue()));
                    artist.setNationality(allData.get(5).varCharValue());
                    artists.add(artist);
                }
            }
        } catch (AthenaException e) {
            logger.error(e.getMessage());
        }
        return artists;
    }
}
