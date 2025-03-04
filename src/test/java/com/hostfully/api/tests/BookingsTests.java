package com.hostfully.api.tests;

import com.hostfully.api.config.BaseTest;

import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.hostfully.api.utils.FileUtils.readJsonFile;
import static io.restassured.RestAssured.given;
import static java.util.Optional.empty;
import static org.hamcrest.Matchers.not;

import java.io.IOException;

public class BookingsTests extends BaseTest {

    private final String GET_BOOKINGS_ENDPOINT = "/bookings";
    private final String POST_BOOKINGS_ENDPOINT = "/bookings";

    @Test
    @DisplayName("GET /bookings unauthorized access")
    public void testGetBookingsUnauthorized() {
        given()
            .auth()
            .basic("invalid", "credentials")
        .when()
            .get(GET_BOOKINGS_ENDPOINT)
        .then()
            .statusCode(401);
    }

    /*
    @Test
    @DisplayName("POST /bookings fails to add inexisting property")
    public void testErrorWhenBookingInexistingProperty() throws IOException {
        given()
                .auth()
                .preemptive()
                .basic(username, password)
                .body(readJsonFile("src/test/resources/requests/bookInexistingProperty.json"))
                .when()
                .post(POST_BOOKINGS_ENDPOINT)
                .then()
                .log()
                .all()
                .statusCode(400);
    }
    */

    @Test
    @DisplayName("GET /bookings returns correct bookings structure")
    public void testValidGetBookingsStructure() throws IOException {
        //TODO: enhance tests by controlling database state, dettaching test scenarios from each other
        given()
            .auth()
            .preemptive()
            .basic(username, password)
        .when()
            .get(GET_BOOKINGS_ENDPOINT)
        .then()
            .statusCode(200)
            .body("$", not(empty()))
            .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/BookingSchemaDTO.json")));
    }

    @Test
    @DisplayName("POST /bookings unauthorized access")
    public void testPostBookingsUnauthorized() throws IOException {
        given()
            .auth()
            .preemptive()
            .basic("invalid", "credentials")
        .when()
            .post(POST_BOOKINGS_ENDPOINT)
        .then()
            .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/BookingErrorSchemaDTO.json")))
            .statusCode(401);
    }

}
