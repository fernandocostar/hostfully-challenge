package com.hostfully.api.tests.booking;

import com.hostfully.api.config.BaseTest;
import com.hostfully.api.helpers.BookingHelper;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.hostfully.api.utils.DateUtils.*;
import static com.hostfully.api.utils.FileUtils.readJsonFile;
import static com.hostfully.api.utils.requests.BookingDTOFactory.*;
import static com.hostfully.api.utils.requests.GuestDTOFactory.createValidGuest;
import static org.hamcrest.CoreMatchers.is;

public class BookingUpdateTests extends BaseTest {

    //TODO: decouple test cases from create valid booking, these endpoints should not depend on external features

    @Test
    @DisplayName("PATCH /bookings/{bookingId}/cancel - cancel booking successfully")
    public void testCancelBookingSuccessfully() throws IOException {
        BookingHelper authorizedBookingHelper = new BookingHelper(username, password);

        //Create booking
        JSONObject requestPayload = createValidBookingPayload();
        Response response = authorizedBookingHelper.createValidBooking(requestPayload);
        String bookingId = response.jsonPath().get("id");

        Response cancelResponse = authorizedBookingHelper.performCancelPatchRequest(bookingId);
        cancelResponse.then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/booking/BookingCreationSchema.json")))
                .body("status", is("CANCELLED"));
    }

    @Test
    @DisplayName("PATCH /bookings/{bookingId}/cancel - cancel booking with invalid bookingId")
    public void testErrorCancelBookingInvalidBookingId() {
        BookingHelper authorizedBookingHelper = new BookingHelper(username, password);
        //TODO: Statuses and messages should be reviewed, returning 400 - Bad Request
        Response cancelResponse = authorizedBookingHelper.performCancelPatchRequest("invalid");
        cancelResponse.then()
                .statusCode(400)
                .body("title", is("Bad Request"));
    }

    @Test
    @DisplayName("PATCH /bookings/{bookingId}/guest - change booking guest successfully")
    public void testChangeBookingGuestSuccessfully() throws IOException {
        BookingHelper authorizedBookingHelper = new BookingHelper(username, password);

        //Create booking
        JSONObject requestPayload = createValidBookingPayload();
        Response response = authorizedBookingHelper.createValidBooking(requestPayload);
        String bookingId = response.jsonPath().get("id");

        JSONObject newGuest = createValidGuest();
        List<Integer> expectedDateOfBirth = castToDateList(newGuest.get("dateOfBirth").toString());

        Response updateGuestResponse = authorizedBookingHelper.performGuestUpdatePatchRequest(bookingId, newGuest);
        updateGuestResponse.then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/booking/BookingCreationSchema.json")))
                .body("guest.firstName", is(newGuest.get("firstName")))
                .body("guest.lastName", is(newGuest.get("lastName")))
                .body("guest.dateOfBirth", is(expectedDateOfBirth));
    }

    @Test
    @DisplayName("PATCH /bookings/{bookingId}/rebook - fail to rebook non-cancelled booking")
    public void testErrorWhenRebookingNonCancelledBooking() throws IOException {
        BookingHelper authorizedBookingHelper = new BookingHelper(username, password);

        //Create booking
        JSONObject requestPayload = createValidBookingPayload();
        Response response = authorizedBookingHelper.createValidBooking(requestPayload);
        String bookingId = response.jsonPath().get("id");

        Response rebookResponse = authorizedBookingHelper.performRebookPatchRequest(bookingId, requestPayload);
        rebookResponse.then()
                .statusCode(422)
                .body("title", is("Invalid Booking Status"))
                .body("detail", is("Booking is not cancelled, cannot rebook"))
                .body("instance", is("/bookings/" + bookingId + "/rebook"))
                .body("CANNOT_REBOOK_NOT_CANCELLED_BOOKING", is("CANNOT_REBOOK_NOT_CANCELLED_BOOKING"));
    }

    @Test
    @DisplayName("PATCH /bookings/{bookingId}/rebook - rebook cancelled booking")
    public void testRebookCancelledBooking() throws IOException {
        BookingHelper authorizedBookingHelper = new BookingHelper(username, password);

        //Create booking
        JSONObject requestPayload = createValidBookingPayload();
        Response response = authorizedBookingHelper.createValidBooking(requestPayload);
        String bookingId = response.jsonPath().get("id");

        //Cancel booking
        authorizedBookingHelper.performCancelPatchRequest(bookingId).then().statusCode(200);

        Response rebookResponse = authorizedBookingHelper.performRebookPatchRequest(bookingId, requestPayload);
        rebookResponse.then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/booking/BookingCreationSchema.json")))
                .body("status", is(requestPayload.get("status")))
                .body("propertyId", is(requestPayload.get("propertyId")));
    }

}