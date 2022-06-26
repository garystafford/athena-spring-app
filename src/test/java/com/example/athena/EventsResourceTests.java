package com.example.athena;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@SpringBootTest
class EventsResourceTests {

    private static final String ResourcePath = "/v1/events";

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
    void findAllWithLimit() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("limit", 3)
                .when()
                .get(ResourcePath)
                .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(3));
    }

    @Test
    void findAllWithOffset() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("offset", 2)
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
                .queryParam("limit", 3)
                .queryParam("offset", 2)
                .when()
                .get(ResourcePath)
                .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(3));
    }

    @Test
    void findById() {
        // Get the first 'id' available
        int id = get(ResourcePath + "?limit=1")
                .then()
                .extract()
                .path("[0].id");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(ResourcePath + "/{id}", id)
                .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(200)
                .body("id", Matchers.equalTo(id));
    }
}
