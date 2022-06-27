package com.example.athena;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@SpringBootTest
class SalesByCategoryResourceTests {

    private static final String ResourcePath = "/v1/salesbycategory";

    private static final int resultsetLimit = 25;

    @Test
    void findAll() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(ResourcePath)
                .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(200)
                .body("$.size()", Matchers.greaterThanOrEqualTo(1))
                .body("$.size()", Matchers.lessThanOrEqualTo(resultsetLimit));
    }

    @Test
    void findAllWithDate() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("date", getSaleDate())
                .when()
                .get(ResourcePath)
                .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(200)
                .body("$.size()", Matchers.greaterThanOrEqualTo(1))
                .body("$.size()", Matchers.lessThanOrEqualTo(resultsetLimit))
                .body("[0].saleAmount", Matchers.greaterThan(0.0f));
    }

    @Test
    void findAllWithLimit() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("limit", 1)
                .when()
                .get(ResourcePath)
                .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(1));
    }

    @Test
    void findAllWithOffset() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("offset", 1)
                .when()
                .get(ResourcePath)
                .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(200)
                .body("$.size()", Matchers.greaterThanOrEqualTo(1))
                .body("$.size()", Matchers.lessThanOrEqualTo(resultsetLimit));
    }

    @Test
    void findAllWithLimitAndOffset() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("limit", 1)
                .queryParam("offset", 1)
                .when()
                .get(ResourcePath)
                .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(1));
    }

    @Test
    void findAllWithDateLimitAndOffset() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("date", getSaleDate())
                .queryParam("limit", 1)
                .queryParam("offset", 1)
                .when()
                .get(ResourcePath)
                .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(1))
                .body("[0].saleAmount", Matchers.greaterThan(0.0f));
    }

    private String getSaleDate() {
        // Get the first saleTime available
        String saleTime = get("/v1/sales?limit=1")
                .then()
                .extract()
                .path("[0].saleTime");

        return LocalDateTime.parse(saleTime).toLocalDate().toString();
    }
}
