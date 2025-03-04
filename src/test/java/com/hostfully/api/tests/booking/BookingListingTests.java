package com.hostfully.api.tests.booking;

import com.hostfully.api.config.BaseTest;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.hostfully.api.utils.FileUtils.readJsonFile;
import static io.restassured.RestAssured.given;
import static java.util.Optional.empty;
import static org.hamcrest.Matchers.not;

public class BookingListingTests extends BaseTest {

    private final String GET_BOOKINGS_ENDPOINT = "/bookings";

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

}
