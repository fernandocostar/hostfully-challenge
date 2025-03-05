package com.hostfully.api.tests.booking;

import com.hostfully.api.config.BaseTest;

import io.restassured.module.jsv.JsonSchemaValidator;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.hostfully.api.utils.FileUtils.readJsonFile;
import static com.hostfully.api.utils.requests.BookingDTOFactory.*;
import static com.hostfully.api.utils.requests.GuestDTOFactory.createValidGuest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;
import java.util.stream.Stream;

public class BookingCreationTests extends BaseTest {

    private final String POST_BOOKINGS_ENDPOINT = "/bookings";

    private static Stream<String> bookingMandatoryAttributes() {
        return Stream.of("startDate", "endDate", "propertyId");
    }
    private static Stream<String> bookingOptionalAttributes() {
        return Stream.of("id", "status", "guest");
    }
    private static Stream<String> guestMandatoryAttributes() {
        return Stream.of("firstName", "lastName");
    }

    @Test
    @DisplayName("POST /bookings fails on unauthorized access")
    public void testErrorUnauthorizedBookingCreation() throws IOException {
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

    @Test
    @DisplayName("POST /bookings fails to add inexisting property")
    public void testErrorWhenBookingInexistingProperty() {

        JSONObject requestPayload = createInexistingBookingPayload();

        given()
            .auth()
            .preemptive()
            .basic(username, password)
            .body(requestPayload.toString())
        .when()
            .post(POST_BOOKINGS_ENDPOINT)
        .then()
            .statusCode(400)
            .body("title", is("Property not found"))
            .body("detail", is("Property with identifier " + requestPayload.get("propertyId") + " could not be found"));
    }

    @Test
    @DisplayName("POST /bookings fails to book with invalid status")
    public void testErrorWhenBookingContainsInvalidStatus(){

        JSONObject requestPayload = createValidBookingPayload();
        requestPayload.remove("status");
        requestPayload.put("status", "INVALID");

        given()
            .auth()
            .preemptive()
            .basic(username, password)
            .body(requestPayload.toString())
        .when()
            .post(POST_BOOKINGS_ENDPOINT)
        .then()
            .statusCode(400)
            .body("title", is("Bad Request"))
            .body("detail", is("Failed to read request"));
    }

    //VALID BOOKING CREATION
    @Test
    @DisplayName("POST /bookings creates a booking without optional attributes")
    public void testCreateBookingSuccessfully() throws IOException {

        JSONObject requestPayload = createValidBookingPayload();

        given()
            .auth()
            .preemptive()
            .basic(username, password)
            .body(requestPayload.toString())
        .when()
            .post(POST_BOOKINGS_ENDPOINT)
        .then()
            .statusCode(201)
            .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/BookingCreationSchema.json")));
    }

    @ParameterizedTest
    @MethodSource("bookingOptionalAttributes")
    @DisplayName("POST /bookings creates a booking without optional attributes")
    public void testCreateBookingWithoutOptionalAttributes(String optionalAttribute) throws IOException {

        JSONObject requestPayload = createValidBookingPayload();
        requestPayload.remove(optionalAttribute);

        given()
            .auth()
            .preemptive()
            .basic(username, password)
            .body(requestPayload.toString())
        .when()
            .post(POST_BOOKINGS_ENDPOINT)
        .then()
            .statusCode(201)
            .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/BookingCreationSchema.json")));
    }

    @ParameterizedTest
    @MethodSource("bookingMandatoryAttributes")
    @DisplayName("POST /bookings fails when missing mandatory attributes")
    public void testErrorWhenBookingMissingMandatoryAttributes(String missingField) {
        JSONObject requestPayload = createValidBookingPayload();
        requestPayload.remove(missingField);

        given()
            .auth()
            .preemptive()
            .basic(username, password)
            .body(requestPayload.toString())
        .when()
            .post(POST_BOOKINGS_ENDPOINT)
        .then()
            .statusCode(400)
            .body("title", is("Validation Error"))
            .body("errors[0].field", is(missingField))
            .body("errors[0].code", is("NotNull"));
    }

    @ParameterizedTest
    @MethodSource("guestMandatoryAttributes")
    @DisplayName("POST /bookings fails when missing mandatory Guest attributes")
    public void testErrorWhenBookingMissingGuestMandatoryAttributes(String missingField) {
        JSONObject guest = createValidGuest();
        guest.remove(missingField);

        JSONObject requestPayload = createBookingPayloadPassingGuest(guest);

        given()
            .auth()
            .preemptive()
            .basic(username, password)
            .body(requestPayload.toString())
        .when()
            .post(POST_BOOKINGS_ENDPOINT)
        .then()
            .statusCode(400)
            .body("title", is("Validation Error"))
            .body("errors[0].field", is("guest." + missingField))
            .body("errors[0].code", is("NotNull"));
    }

    //TESTS HAVE DEPENDENCY AMONG FEATURES - BETTER WAY WOULD BE DOING INTEGRATION TESTS OR DB INJECTION IN PROPER ENVIRONMENT

    //BOOKING OVERLAPPING DATES (BOUNDARY + CONTAINS)
    //PATCH ENDPOINTS

}