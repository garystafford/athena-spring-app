package com.example.athena;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@SpringBootTest
class BuyerLikesByCategoryResourceTests {

    private static final String ResourcePath = "/v1/buyerlikes";

    @Test
    void get() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(ResourcePath)
                .then()
                .assertThat()
                .contentType(JSON)
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(1));
    }
}
