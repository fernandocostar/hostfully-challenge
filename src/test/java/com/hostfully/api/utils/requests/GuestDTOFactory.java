package com.hostfully.api.utils.requests;

import com.github.javafaker.Faker;
import org.json.JSONObject;
import java.util.UUID;

public class GuestDTOFactory {

    private static final Faker faker = new Faker();

    public static JSONObject createValidGuest() {
        Faker faker = new Faker();

        return new JSONObject()
                .put("firstName", faker.name().firstName())
                .put("lastName", faker.name().lastName())
                .put("dateOfBirth", "2025-03-04");
    }
}