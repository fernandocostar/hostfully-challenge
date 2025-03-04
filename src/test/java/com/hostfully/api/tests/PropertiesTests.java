package com.hostfully.api.tests;

import com.hostfully.api.config.BaseTest;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.hostfully.api.utils.FileUtils.readJsonFile;
import static io.restassured.RestAssured.given;
import static java.util.Optional.empty;
import static org.hamcrest.Matchers.not;

public class PropertiesTests extends BaseTest {

    private final String GET_PROPERTIES_ENDPOINT = "/properties";

    @Test
    @DisplayName("GET /properties unauthorized access")
    public void testGetPropertiesUnauthorized() {
        given()
            .auth()
            .basic("invalid", "credentials")
        .when()
            .get(GET_PROPERTIES_ENDPOINT)
        .then()
            .statusCode(401);
    }

    @Test
    @DisplayName("GET /properties returns correct properties structure")
    public void testValidGetPropertiesStructure() throws IOException {
        //TODO: enhance tests by controlling database state, dettaching test scenarios from each other
        given()
            .auth()
            .preemptive()
            .basic(username, password)
        .when()
            .get(GET_PROPERTIES_ENDPOINT)
        .then()
            .statusCode(200)
            .body("$", not(empty()))
            .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/PropertiesDTO.json")));
    }

    //POST PROPERTIES: MISSING MANDATORY ATTRIBUTES
    //POST PROPERTIES: INVALID MANDATORY ATTRIBUTES
    //POST PROPERTIES: INVALID OPTIONAL ATTRIBUTES
    //POST PROPERTIES: VALID REQUEST WITHOUT OPTIONAL ATTRIBUTES
    //POST PROPERTIES: VALID REQUEST WITH OPTIONAL ATTRIBUTES

    //GET PROPERTY BY ID: UNAUTHORIZED
    //GET PROPERTY BY ID: VALID REQUEST
    //GET PROPERTY BY ID: INVALID ID
    //GET PROPERTY BY ID: NON-EXISTING ID

}
