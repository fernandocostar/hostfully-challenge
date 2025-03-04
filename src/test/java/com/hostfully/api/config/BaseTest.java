package com.hostfully.api.config;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import io.restassured.RestAssured;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BaseTest {

    protected static final String BASE_URL = "https://qa-assessment.svc.hostfully.com";

    protected static String username;
    protected static String password;

    @BeforeAll
    public static void setUp() throws IOException {

        //TODO: load username from environment variable - choose to not overengineer the project at this
        Properties properties = new Properties();

        try (InputStream input = BaseTest.class.getClassLoader().getResourceAsStream("api-test.properties")) {
            if (input == null) {
                throw new IllegalStateException("Unable to find config.properties in classpath");
            }
            properties.load(input);
        }

        username = properties.getProperty("api.username");
        password = properties.getProperty("api.password");

        if (username == null || password == null) {
            throw new IllegalStateException("Missing api.username or api.password in config.properties");
        }

        RestAssured.baseURI = BASE_URL;
        RestAssured.requestSpecification = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }
}
