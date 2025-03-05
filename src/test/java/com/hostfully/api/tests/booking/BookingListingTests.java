package com.hostfully.api.tests.booking;

import com.hostfully.api.config.BaseTest;
import com.hostfully.api.helpers.BookingHelper;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.hostfully.api.utils.FileUtils.readJsonFile;
import static java.util.Optional.empty;
import static org.hamcrest.Matchers.not;

public class BookingListingTests extends BaseTest {

    @Test
    @DisplayName("GET /bookings unauthorized access")
    public void testGetBookingsUnauthorized() {
        BookingHelper unauthorizedBookingHelper = new BookingHelper("invalid", "credentials");
        Response response = unauthorizedBookingHelper.performGetRequest();
        response.then()
            .statusCode(401);
    }

    @Test
    @DisplayName("GET /bookings returns correct bookings structure")
    public void testValidGetBookingsStructure() throws IOException {
        BookingHelper authorizedBookingHelper = new BookingHelper(username, password);
        Response response = authorizedBookingHelper.performGetRequest();
        response.then()
            .statusCode(200)
            .body("$", not(empty()))
            .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/booking/BookingListSchema.json")));
    }

}
