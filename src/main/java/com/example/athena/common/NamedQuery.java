package com.example.athena.common;

import com.example.athena.config.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryRequest;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryResponse;

@Component
public class NamedQuery {

    private static final Logger logger = LoggerFactory.getLogger(NamedQuery.class);

    private final AthenaClientFactory athenaClientFactoryImp;

    private final ConfigProperties configProperties;

    @Autowired
    public NamedQuery(AthenaClientFactory athenaClientFactoryImp, ConfigProperties configProperties) {
        this.athenaClientFactoryImp = athenaClientFactoryImp;
        this.configProperties = configProperties;
    }

//    public void GetNamedQuery() {
//        try (AthenaClient athenaClient = athenaClientFactoryImp.createClient(configProperties.getRegion(), configProperties.getIamProfile())) {
//            try {
//                ListNamedQueriesRequest listNamedQueriesRequest = ListNamedQueriesRequest.builder()
//                        .build();
//
//                ListNamedQueriesIterable listNamedQueriesResponses = athenaClient.listNamedQueriesPaginator(listNamedQueriesRequest);
//                for (ListNamedQueriesResponse listNamedQueriesResponse : listNamedQueriesResponses) {
//                    List<String> namedQueryIds = listNamedQueriesResponse.namedQueryIds();
//                    System.out.println(namedQueryIds);
//                }
//            } catch (Exception e) {
//                logger.error(e.getMessage());
//            }
//        }
//    }

    public void CreateNamedQuery() {
        // Method is not as efficient as it could be.
        // Create new named query each time applications starts.
        // Also, doesn't delete previous named queries.

        String namedQueryName = "buyer_likes_by_category";

        String namedQuerySql = """
                WITH buyers AS (
                	SELECT DISTINCT(buyerid) AS id
                	FROM refined_tickit_public_sales
                )
                SELECT sum(cast(likesports as integer)) AS sports,
                	sum(cast(liketheatre as integer)) AS theatre,
                	sum(cast(likeconcerts as integer)) AS concerts,
                	sum(cast(likejazz as integer)) AS jazz,
                	sum(cast(likeclassical as integer)) AS classical,
                	sum(cast(likeopera as integer)) AS opera,
                	sum(cast(likerock as integer)) AS rock,
                	sum(cast(likevegas as integer)) AS vegas,
                	sum(cast(likebroadway as integer)) AS broadway,
                	sum(cast(likemusicals as integer)) AS musicals
                FROM refined_tickit_public_users u,
                	buyers b
                WHERE u.userid = b.id;""";

        try (AthenaClient athenaClient = athenaClientFactoryImp.createClient(configProperties.getRegion(), configProperties.getIamProfile())) {
            CreateNamedQueryRequest createNamedQueryRequest = CreateNamedQueryRequest.builder()
                    .name(namedQueryName)
                    .description("Returns sums of all buyer's likes")
                    .workGroup(configProperties.getWorkGroup())
                    .database(configProperties.getDatabase())
                    .queryString(namedQuerySql)
                    .build();
            CreateNamedQueryResponse createNamedQueryResponse = athenaClient.createNamedQuery(createNamedQueryRequest);
            String namedQueryId = createNamedQueryResponse.namedQueryId();
            System.setProperty("namedQueryId", namedQueryId); // retrieved by service
            logger.debug(String.format("Named query created successfully with Id: %s", namedQueryId));
        }
    }
}
