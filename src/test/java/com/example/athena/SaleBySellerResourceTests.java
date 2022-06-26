package com.example.athena;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@SpringBootTest
class SaleBySellerResourceTests {

    private static final String ResourcePath = "/v1/salesbyseller";

    private static final int resultsetLimit = 25;

    @Test
    void findById() {
        // Get the first sellerId available
        int id = get( "/v1/sales?limit=1")
                .then()
                .extract()
                .path("[0].sellerId");

        // Get that seller's last name
        String lastName = get( "/v1/users/{id}",  id)
                .then()
                .extract()
                .path("lastName");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(ResourcePath + "/{id}", id)
                .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(200)
                .body("[0].seller", Matchers.endsWith(lastName))
                .body("$.size()", Matchers.greaterThanOrEqualTo(1))
                .body("$.size()", Matchers.lessThanOrEqualTo(resultsetLimit));
    }
}
