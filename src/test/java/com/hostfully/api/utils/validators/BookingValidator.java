package com.hostfully.api.utils.validators;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Map;

public class BookingValidator {

    public static void validateBooking(Map<String, Object> booking) {
        assertNotNull(booking, "Booking object should not be null");

        String id = (String) booking.get("id");
        assertTrue(isValidUUID(id), "id should be a valid UUID");

        String startDate = (String) booking.get("startDate");
        String endDate = (String) booking.get("endDate");
        assertTrue(isValidDate(startDate), "startDate should be a valid date");
        assertTrue(isValidDate(endDate), "endDate should be a valid date");

        String status = (String) booking.get("status");
        assertThat("status should be one of SCHEDULED, CANCELLED, COMPLETED", status, oneOf("SCHEDULED", "CANCELLED", "COMPLETED"));

        Map<String, Object> guest = (Map<String, Object>) booking.get("guest");
        assertNotNull(guest, "Guest object should be present");

        String guestFirstName = (String) guest.get("firstName");
        String guestLastName = (String) guest.get("lastName");
        String guestDob = (String) guest.get("dateOfBirth");

        assertNotNull(guestFirstName, "Guest's firstName should be present");
        assertNotNull(guestLastName, "Guest's lastName should be present");
        assertThat("Guest's firstName should not be empty", guestFirstName.length(), greaterThan(0));
        assertThat("Guest's lastName should not be empty", guestLastName.length(), greaterThan(0));

        assertTrue(isValidDate(guestDob), "Guest's dateOfBirth should be a valid date");

        String propertyId = (String) booking.get("propertyId");
        assertTrue(isValidUUID(propertyId), "propertyId should be a valid UUID");
    }

    private static boolean isValidUUID(String uuid) {
        try {
            java.util.UUID.fromString(uuid);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isValidDate(String dateStr) {
        try {
            java.time.LocalDate.parse(dateStr);
            return true;
        } catch (java.time.format.DateTimeParseException e) {
            return false;
        }
    }
}
