package com.hostfully.api.tests;

import com.hostfully.api.config.BaseTest;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

public class BookingsTests extends BaseTest {

    private final String GET_BOOKINGS_ENDPOINT = "/bookings";

    @Test
    public void testGetBookingsSuccess() {

        Response response = given()
                .auth()
                .preemptive()
                .basic(username, password)
                .when()
                .get(GET_BOOKINGS_ENDPOINT)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

    }

}
