package com.hostfully.api.utils.requests;

import com.github.javafaker.Faker;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.UUID;

import static com.hostfully.api.utils.requests.GuestDTOFactory.createValidGuest;

public class BookingDTOFactory {

    private static final String VALID_EXISTING_PROPERTY_ID = "7a58559f-1a64-4c24-a4f7-28a6b81302cc";

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
        return createBookingPayloadPassingGuest(createValidGuest());
    }

    public static JSONObject createBookingPayloadPassingGuest(JSONObject guest) {
        LocalDate startDate = getStartDate();
        return new JSONObject()
                .put("id", UUID.randomUUID().toString())
                .put("startDate", startDate)
                .put("endDate", getEndDate(startDate))
                .put("status", "SCHEDULED")
                .put("guest", guest)
                .put("propertyId", VALID_EXISTING_PROPERTY_ID);
    }

    private static LocalDate getStartDate() {
        return LocalDate.now().plusDays(faker.number().numberBetween(1, 10000));
    }

    private static LocalDate getEndDate(LocalDate startDate) {
        return startDate.plusDays(faker.number().numberBetween(1, 2));
    }

}
