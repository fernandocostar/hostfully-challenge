package com.hostfully.api.tests.booking;

import com.hostfully.api.config.BaseTest;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static com.hostfully.api.utils.DateUtils.*;
import static com.hostfully.api.utils.FileUtils.readJsonFile;
import static com.hostfully.api.utils.requests.BookingDTOFactory.*;
import static com.hostfully.api.utils.requests.GuestDTOFactory.createValidGuest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class BookingUpdateTests extends BaseTest {

    private final String POST_BOOKINGS_ENDPOINT = "/bookings";
    private final String PATCH_CANCEL_BOOKING_ENDPOINT = "/bookings/{bookingId}/cancel";
    private final String PATCH_GUEST_BOOKING_ENDPOINT = "/bookings/{bookingId}/guest";
    private final String PATCH_REOOKING_ENDPOINT = "/bookings/{bookingId}/rebook";

    private Response createValidBooking(JSONObject requestPayload) throws IOException {

        String startDate = requestPayload.get("startDate").toString();
        String endDate = requestPayload.get("endDate").toString();

        List<Integer> expectedStartDate = castToDateList(startDate);
        List<Integer> expectedEndDate = castToDateList(endDate);

        return given()
            .auth()
            .preemptive()
            .basic(username, password)
            .body(requestPayload.toString())
        .when()
            .post(POST_BOOKINGS_ENDPOINT)
        .then()
            .statusCode(201)
            .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/BookingCreationSchema.json")))
            .body("status", is(requestPayload.get("status")))
            .body("propertyId", is(requestPayload.get("propertyId")))
            .body("startDate", is(expectedStartDate))
            .body("endDate", is(expectedEndDate))
            .extract().response();
    }

    //TODO: decouple test cases from create valid booking, these endpoints should not depend on external features

    @Test
    @DisplayName("PATCH /bookings/{bookingId}/cancel - cancel booking successfully")
    public void testCancelBookingSuccessfully() throws IOException {
        //Create booking
        JSONObject requestPayload = createValidBookingPayload();
        Response response = createValidBooking(requestPayload);
        String bookingId = response.jsonPath().get("id");

        given()
            .auth()
            .preemptive()
            .basic(username, password)
        .when()
            .patch(PATCH_CANCEL_BOOKING_ENDPOINT.replace("{bookingId}", bookingId))
        .then()
            .statusCode(200)
            .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/BookingCreationSchema.json")))
            .body("status", is("CANCELLED"));
    }

    @Test
    @DisplayName("PATCH /bookings/{bookingId}/cancel - cancel booking with invalid bookingId")
    public void testErrorCancelBookingInvalidBookingId() {
        //TODO: Statuses and messages should be reviewed, returning 400 - Bad Request
        given()
            .auth()
            .preemptive()
            .basic(username, password)
        .when()
            .patch(PATCH_CANCEL_BOOKING_ENDPOINT.replace("{bookingId}", "invalid"))
        .then()
            .statusCode(400)
            .body("title", is("Bad Request"));
    }

    @Test
    @DisplayName("PATCH /bookings/{bookingId}/guest - change booking guest successfully")
    public void testChangeBookingGuestSuccessfully() throws IOException {
        //Create booking
        JSONObject requestPayload = createValidBookingPayload();
        Response response = createValidBooking(requestPayload);
        String bookingId = response.jsonPath().get("id");

        JSONObject newGuest = createValidGuest();
        List<Integer> expectedDateOfBirth = castToDateList(newGuest.get("dateOfBirth").toString());

        given()
            .auth()
            .preemptive()
            .basic(username, password)
            .body(newGuest.toString())
        .when()
            .patch(PATCH_GUEST_BOOKING_ENDPOINT.replace("{bookingId}", bookingId))
        .then()
            .statusCode(200)
            .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/BookingCreationSchema.json")))
            .body("guest.firstName", is(newGuest.get("firstName")))
            .body("guest.lastName", is(newGuest.get("lastName")))
            .body("guest.dateOfBirth", is(expectedDateOfBirth));
    }

    @Test
    @DisplayName("PATCH /bookings/{bookingId}/rebook - fail to rebook non-cancelled booking")
    public void testErrorWhenRebookingNonCancelledBooking() throws IOException {
        //Create booking
        JSONObject requestPayload = createValidBookingPayload();
        Response response = createValidBooking(requestPayload);
        String bookingId = response.jsonPath().get("id");

        given()
            .auth()
            .preemptive()
            .basic(username, password)
            .body(requestPayload.toString())
        .when()
            .patch(PATCH_REOOKING_ENDPOINT.replace("{bookingId}", bookingId))
        .then()
            .statusCode(422)
            .body("title", is("Invalid Booking Status"))
            .body("detail", is("Booking is not cancelled, cannot rebook"))
            .body("instance", is("/bookings/" + bookingId + "/rebook"))
            .body("CANNOT_REBOOK_NOT_CANCELLED_BOOKING", is("CANNOT_REBOOK_NOT_CANCELLED_BOOKING"));
    }

}