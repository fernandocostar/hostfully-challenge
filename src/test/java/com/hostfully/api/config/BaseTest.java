package com.hostfully.api.config;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BaseTest {

    private static final Log log = LogFactory.getLog(BaseTest.class);
    private static TestInfo testInfo;

    protected static final String BASE_URL = "https://qa-assessment.svc.hostfully.com";

    protected static String username;
    protected static String password;

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        log.info("Starting test: " + testInfo.getDisplayName());
    }

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
        RestAssured.filters(new PrettyLoggingFilter());

    }

    @AfterEach
    public void afterEach(TestInfo testInfo) {
        log.info("Finished test: " + testInfo.getDisplayName());
    }

}
