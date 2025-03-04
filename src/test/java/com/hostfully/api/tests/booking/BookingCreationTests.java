package com.hostfully.api.tests.booking;

import com.hostfully.api.config.BaseTest;

import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.hostfully.api.utils.FileUtils.readJsonFile;
import static io.restassured.RestAssured.given;

import java.io.IOException;

public class BookingCreationTests extends BaseTest {

    private final String POST_BOOKINGS_ENDPOINT = "/bookings";

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

    @Disabled
    @Test
    @DisplayName("POST /bookings fails to add inexisting property") //TODO: fix and enable test
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

}