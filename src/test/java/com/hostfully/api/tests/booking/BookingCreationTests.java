package com.hostfully.api.tests.booking;

import com.hostfully.api.config.BaseTest;

import io.restassured.module.jsv.JsonSchemaValidator;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.hostfully.api.utils.FileUtils.readJsonFile;
import static com.hostfully.api.utils.requests.BookingDTOFactory.createInexistingBookingPayload;
import static com.hostfully.api.utils.requests.BookingDTOFactory.createValidBookingPayload;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;
import java.util.stream.Stream;

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

    @Test
    @DisplayName("POST /bookings fails to add inexisting property")
    public void testErrorWhenBookingInexistingProperty() throws IOException {

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
    @DisplayName("POST /bookings creates a booking with mandatory attributes only") //TODO: review and fix
    public void testCreateBookingWithMandatoryAttributes() throws IOException {

        JSONObject requestPayload = createValidBookingPayload();

        requestPayload.remove("id");
        requestPayload.remove("status");
        requestPayload.remove("guest");

        given()
            .auth()
            .preemptive()
            .basic(username, password)
            .body(requestPayload.toString()).log().all()
        .when()
            .post(POST_BOOKINGS_ENDPOINT)
        .then()
            .statusCode(201)
            .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/BookingCreationSchema.json")));
    }

    @ParameterizedTest
    @MethodSource("bookingMandatoryFields")
    @DisplayName("POST /bookings fails when missing mandatory attributes")
    public void testErrorWhenBookingMissingMandatoryAttributes(String missingField) {
        JSONObject requestPayload = createValidBookingPayload();
        requestPayload.remove(missingField); // Remove the field dynamically

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

    private static Stream<String> bookingMandatoryFields() {
        return Stream.of("startDate", "endDate", "propertyId");
    }

}