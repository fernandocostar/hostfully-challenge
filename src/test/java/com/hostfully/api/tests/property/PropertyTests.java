package com.hostfully.api.tests.property;

import com.hostfully.api.config.BaseTest;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.hostfully.api.utils.FileUtils.readJsonFile;
import static io.restassured.RestAssured.given;
import static java.util.Optional.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class PropertyTests extends BaseTest {

    private final String GET_PROPERTIES_ENDPOINT = "/properties";
    private final String POST_PROPERTIES_ENDPOINT = "/properties";

    //GET Properties endpoint

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

    //POST Properties endpoint

    @Test
    @DisplayName("POST /properties requires mandatory attributes")
    public void testCreatePropertyRequiresMandatoryAttributes() throws IOException {
        given()
            .auth()
            .preemptive()
            .basic(username, password)
            .body(readJsonFile("src/test/resources/requests/PropertyCreationMissingAlias.json"))
        .when()
            .post(POST_PROPERTIES_ENDPOINT)
        .then()
            .statusCode(400)
            .body("title", is("Validation Error"))
            .body("detail", is("Validation failed"))
            .body("errors[0].field", is("alias"))
            .body("errors[0].code", is("NotNull"))
            .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/ValidationErrorDTO.json")));
    }

    @Test
    @DisplayName("POST /properties sending invalid mandatory attributes")
    public void testCreatePropertySendingInvalidAttributes() throws IOException {
        given()
            .auth()
            .preemptive()
            .basic(username, password)
            .body(readJsonFile("src/test/resources/requests/PropertyCreationSendingInvalidMandatoryAttribute.json"))
        .when()
            .post(POST_PROPERTIES_ENDPOINT)
        .then()
            .statusCode(400)
            .body("title", is("Validation Error"))
            .body("detail", is("Invalid attribute type"));
    }


    //POST PROPERTIES: INVALID OPTIONAL ATTRIBUTES
    //POST PROPERTIES: VALID REQUEST WITHOUT OPTIONAL ATTRIBUTES
    //POST PROPERTIES: VALID REQUEST WITH OPTIONAL ATTRIBUTES

    //GET PROPERTY BY ID: UNAUTHORIZED
    //GET PROPERTY BY ID: VALID REQUEST
    //GET PROPERTY BY ID: INVALID ID
    //GET PROPERTY BY ID: NON-EXISTING ID

}
