package com.hostfully.api.utils.requests;

import com.github.javafaker.Faker;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.UUID;

import static com.hostfully.api.utils.requests.GuestDTOFactory.createValidGuest;

public class BookingDTOFactory {

    private static final String VALID_EXISTING_PROPERTY_ID = "a05018e8-c9f4-430c-8de2-abc3a8e58c21";

    private static final Faker faker = new Faker();

    public static JSONObject createInexistingBookingPayload() {
        return new JSONObject()
                .put("id", UUID.randomUUID().toString())
                .put("startDate", "2025-03-04")
                .put("endDate", "2025-03-04")
                .put("status", "SCHEDULED")
                .put("guest", createValidGuest())
                .put("propertyId", UUID.randomUUID().toString());
    }

    public static JSONObject createValidBookingPayload() {
        LocalDate startDate = getStartDate();
        return new JSONObject()
                .put("id", UUID.randomUUID().toString())
                .put("startDate", startDate)
                .put("endDate", getEndDate(startDate))
                .put("status", "SCHEDULED")
                .put("guest", createValidGuest())
                .put("propertyId", VALID_EXISTING_PROPERTY_ID);
    }

    private static LocalDate getStartDate() {
        return LocalDate.now().plusDays(faker.number().numberBetween(1, 1000));
    }

    private static LocalDate getEndDate(LocalDate startDate) {
        return startDate.plusDays(faker.number().numberBetween(1, 2));
    }

}
