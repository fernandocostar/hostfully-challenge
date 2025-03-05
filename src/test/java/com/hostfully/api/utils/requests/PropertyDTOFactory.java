package com.hostfully.api.utils.requests;

import com.github.javafaker.Faker;
import org.json.JSONObject;

import java.util.UUID;

public class PropertyDTOFactory {

    private static final Faker faker = new Faker();

    private static final String VALID_COUNTRY_CODE = "US";

    public static JSONObject createValidPropertyPayload() {
        return new JSONObject()
                .put("id", UUID.randomUUID().toString())
                .put("alias", faker.address().fullAddress())
                .put("countryCode", VALID_COUNTRY_CODE)
                .put("createdAt", "2025-03-05T21:19:44.631Z");
    }

    public static JSONObject createRequiredOnlyPropertyPayload() {
        return new JSONObject()
                .put("alias", faker.address().fullAddress());
    }

}
