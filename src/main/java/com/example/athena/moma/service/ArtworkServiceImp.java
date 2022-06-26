package com.example.athena.moma.service;

import com.example.athena.common.AthenaClientFactory;
import com.example.athena.common.AthenaCommon;
import com.example.athena.config.ConfigProperties;
import com.example.athena.moma.model.Artwork;
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
public class ArtworkServiceImp implements ArtworkService {

    private static final Logger logger = LoggerFactory.getLogger(ArtworkService.class);

    private final ConfigProperties configProperties;

    private final AthenaClientFactory athenaClientFactoryImp;

    private final AthenaCommon athenaCommon;

    @Autowired
    public ArtworkServiceImp(ConfigProperties configProperties, AthenaClientFactory athenaClientFactoryImp, AthenaCommon athenaCommon) {
        this.configProperties = configProperties;
        this.athenaClientFactoryImp = athenaClientFactoryImp;
        this.athenaCommon = athenaCommon;
    }

    public List<Artwork> findAll(Integer artistId, String classification, Integer limit, Integer offset) {

        if (limit == null || limit < 1 || limit > configProperties.getLimit()) {
            limit = configProperties.getLimit();
        }

        if (offset == null || offset < 1) {
            offset = 0;
        }

        String whereClause = "WHERE artwork_id IS NOT NULL";
        if (artistId != null) {
            whereClause = whereClause + " AND artist_id=" + artistId;
        }
        if (classification != null) {
            whereClause = whereClause + " AND classification='" + classification + "'";
        }

        String query = String.format("""
                SELECT *
                FROM artworks_refined_glue
                %s
                ORDER BY artwork_id
                OFFSET %s
                LIMIT %s;""", whereClause, offset, limit);


        return startQuery(query);
    }

    public Artwork findById(int id) {

        String query = String.format("""
                SELECT  DISTINCT *
                FROM artworks_refined_glue
                WHERE artwork_id=%s""", id);

        return startQuery(query).get(0);
    }

    // Submits a sample query to Amazon Athena and returns the execution ID of the query
    private List<Artwork> startQuery(String query) {

        logger.debug(String.format("Query: %s", query));

        AthenaClient athenaClient = athenaClientFactoryImp.createClient(configProperties.getRegion(), configProperties.getIamProfile());
        String queryExecutionId = athenaCommon.submitAthenaQuery(athenaClient, query);
        athenaCommon.waitForQueryToComplete(athenaClient, queryExecutionId);
        List<Artwork> artworks = processResultRows(athenaClient, queryExecutionId);
        athenaClient.close();

        return artworks;
    }

    // This code retrieves the results of a query
    private List<Artwork> processResultRows(AthenaClient athenaClient, String queryExecutionId) {

        List<Artwork> artworks = new ArrayList<>();

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
                    Artwork artwork = new Artwork();
                    artwork.setId(parseInt(allData.get(0).varCharValue()));
                    artwork.setArtistId(parseInt(allData.get(1).varCharValue()));
                    artwork.setTitle(allData.get(2).varCharValue());
                    artwork.setDate(allData.get(3).varCharValue() == null ? null : parseInt(allData.get(3).varCharValue()));
                    artwork.setMedium(allData.get(4).varCharValue());
                    artwork.setCatalogue(allData.get(5).varCharValue());
                    artwork.setDepartment(allData.get(6).varCharValue());
                    artwork.setClassification(allData.get(7).varCharValue());
                    artworks.add(artwork);
                }
            }
        } catch (AthenaException e) {
            logger.error(e.getMessage());
        }
        return artworks;
    }

}
