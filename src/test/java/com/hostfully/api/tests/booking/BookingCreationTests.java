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

import static com.hostfully.api.utils.DateUtils.*;
import static com.hostfully.api.utils.DateUtils.getStartDate;
import static com.hostfully.api.utils.FileUtils.readJsonFile;
import static com.hostfully.api.utils.requests.BookingDTOFactory.*;
import static com.hostfully.api.utils.requests.GuestDTOFactory.createValidGuest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public class BookingCreationTests extends BaseTest {

    private final String POST_BOOKINGS_ENDPOINT = "/bookings";

    private static Stream<String> bookingMandatoryAttributes() {
        return Stream.of("startDate", "endDate", "propertyId");
    }
    private static Stream<String> bookingOptionalAttributes() {
        return Stream.of("status", "guest");
    }
    private static Stream<String> guestMandatoryAttributes() {
        return Stream.of("firstName", "lastName");
    }

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

    @Test
    @DisplayName("POST /bookings creates a booking successfully")
    public void testCreateBookingSuccessfully() throws IOException {
        JSONObject requestPayload = createValidBookingPayload();
        JSONObject requestGuest = (JSONObject) requestPayload.get("guest");

        List<Integer> expectedStartDate = castToDateList(requestPayload.get("startDate").toString());
        List<Integer> expectedEndDate = castToDateList(requestPayload.get("endDate").toString());
        List<Integer> expectedGuestDateOfBirth = castToDateList(requestGuest.get("dateOfBirth").toString());

        given()
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
            .body("guest.firstName", is(requestGuest.get("firstName")))
            .body("guest.lastName", is(requestGuest.get("lastName")))
            .body("guest.dateOfBirth", is(expectedGuestDateOfBirth));
    }

    @ParameterizedTest
    @MethodSource("bookingOptionalAttributes")
    @DisplayName("POST /bookings creates a booking without optional attributes")
    public void testCreateBookingWithoutOptionalAttributes(String optionalAttribute) throws IOException {
        JSONObject requestPayload = createValidBookingPayload();
        requestPayload.remove(optionalAttribute);

        List<Integer> expectedStartDate = castToDateList(requestPayload.get("startDate").toString());
        List<Integer> expectedEndDate = castToDateList(requestPayload.get("endDate").toString());

        given()
            .auth()
            .preemptive()
            .basic(username, password)
            .body(requestPayload.toString())
        .when()
            .post(POST_BOOKINGS_ENDPOINT)
        .then()
            .statusCode(201)
            .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/BookingCreationSchema.json")))
            .body("propertyId", is(requestPayload.get("propertyId")))
            .body("startDate", is(expectedStartDate))
            .body("endDate", is(expectedEndDate));
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

    //TESTS HAVE DEPENDENCY AMONG FEATURES - BETTER WAY WOULD BE DOING INTEGRATION TESTS OR DB INJECTION IN A PROPER ENVIRONMENT

    @Test
    @DisplayName("POST /bookings - create booking for property starting after previous booking ends")
    public void testCreateTwoBookingsSecondAfterFirst() throws IOException {
        //First booking
        JSONObject requestPayload = createValidBookingPayload();
        createValidBooking(requestPayload);

        //Switching dates to create a second booking
        requestPayload.put("startDate", requestPayload.get("endDate"));
        requestPayload.put("endDate", getEndDate((LocalDate) requestPayload.get("startDate")));
        List<Integer> secondExpectedStartDate = castToDateList(requestPayload.get("startDate").toString());
        List<Integer> secondExpectedEndDate = castToDateList(requestPayload.get("endDate").toString());

        //Second booking
        createValidBooking(requestPayload);
    }

    @Test
    @DisplayName("POST /bookings - create booking for property finishing before next booking starts")
    public void testCreateTwoBookingsSecondBeforeFirst() throws IOException {
        //First booking
        JSONObject requestPayload = createValidBookingPayload();
        createValidBooking(requestPayload);

        //Switching dates to create a second booking
        requestPayload.put("endDate", requestPayload.get("startDate"));
        requestPayload.put("startDate", calculateStartDateFromEndDate((LocalDate) requestPayload.get("endDate")));
        List<Integer> secondExpectedStartDate = castToDateList(requestPayload.get("startDate").toString());
        List<Integer> secondExpectedEndDate = castToDateList(requestPayload.get("endDate").toString());

        //Second booking
        createValidBooking(requestPayload);
    }

    // Generates combinations of booking dates to test different booking overlaps
    private static Stream<Arguments> generateBookingDateCombinations() {
        //Variation 1
        // Generate random start and end dates for the first booking
        LocalDate startDate11 = getStartDate();
        LocalDate endDate11 = getEndDate(startDate11);

        // Second booking starts the day after the first booking ends
        LocalDate startDate12 = addDaysToDate(endDate11, -1);
        LocalDate endDate12 = getEndDate(startDate12);

        //Variation 2
        // Generate random start and end dates for the second booking
        LocalDate startDate21 = getStartDate();
        LocalDate endDate21 = getEndDate(startDate21);

        // Second booking starts the day after the first booking ends
        LocalDate startDate22 = addDaysToDate(startDate21, -1);
        LocalDate endDate22 = addDaysToDate(endDate21, 1);

        // Define test data for different combinations
        // First variation: Testing border overlap: second booking partially overlaps with the first - example: first booking 24-03 to 27-03, second booking 26-03 to 29-03
        // Second variation: Fully overlapping bookings: second booking fully overlaps - example: first booking 24-03 to 27-03, second booking 22-03 to 28-03

        return Stream.of(
                Arguments.of(startDate11.toString(), endDate11.toString(), startDate12.toString(), endDate12.toString()),
                Arguments.of(startDate21.toString(), endDate21.toString(), startDate22.toString(), endDate22.toString())
        );
    }

    @ParameterizedTest(name = "{index} - First booking with startDate: {0}, endDate: {1} and Second booking with startDate: {2}, endDate: {3}")
    @MethodSource("generateBookingDateCombinations")
    @DisplayName("POST /bookings - test multiple booking overlaps")
    public void testBookingOverlap(String firstStartDate, String firstEndDate, String secondStartDate, String secondEndDate) throws IOException {

        //First booking
        JSONObject firstBookingPayload = createValidBookingPayload();
        firstBookingPayload.put("startDate", firstStartDate);
        firstBookingPayload.put("endDate", firstEndDate);
        createValidBooking(firstBookingPayload);

        // Second booking
        JSONObject secondBookingPayload = new JSONObject(firstBookingPayload.toString());
        secondBookingPayload.put("startDate", secondStartDate);
        secondBookingPayload.put("endDate", secondEndDate);

        given()
            .auth()
            .preemptive()
            .basic(username, password)
            .body(secondBookingPayload.toString())
        .when()
            .post(POST_BOOKINGS_ENDPOINT)
        .then()
            .statusCode(422)
            .body("title", is("Invalid Booking"))
            .body("detail", is("Supplied booking is not valid"))
            .body("BOOKING_DATES_UNAVAILABLE", is("BOOKING_DATES_UNAVAILABLE"));
    }

    //TODO: BOOK OVERLAPPING CANCELLED BOOKING - WOULD USE DIFFERENT FEATURES
}