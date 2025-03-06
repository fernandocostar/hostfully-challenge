package com.hostfully.api.tests.property;

import com.hostfully.api.config.BaseTest;
import com.hostfully.api.helpers.PropertyHelper;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;

import static com.hostfully.api.utils.FileUtils.readJsonFile;
import static com.hostfully.api.utils.requests.PropertyDTOFactory.createRequiredOnlyPropertyPayload;
import static com.hostfully.api.utils.requests.PropertyDTOFactory.createValidPropertyPayload;
import static org.hamcrest.Matchers.is;

public class PropertyCreationTests extends BaseTest {

    @Test
    @DisplayName("POST /properties requires mandatory attributes")
    public void testCreatePropertyRequiresMandatoryAttributes() throws IOException {
        PropertyHelper authorizedPropertyHelper = new PropertyHelper(username, password);

        JSONObject propertyPayload = createValidPropertyPayload();
        propertyPayload.remove("alias");

        Response response = authorizedPropertyHelper.performCreationPostRequest(propertyPayload);
        response.then()
                .statusCode(400)
                .body("title", is("Validation Error"))
                .body("detail", is("Validation failed"))
                .body("errors[0].field", is("alias"))
                .body("errors[0].code", is("NotNull"))
                .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/common/ValidationErrorSchema.json")));
    }

    @Test
    @DisplayName("POST /properties sending invalid mandatory attributes")
    public void testCreatePropertySendingInvalidAttributes() throws IOException {
        PropertyHelper authorizedPropertyHelper = new PropertyHelper(username, password);

        JSONObject propertyPayload = createValidPropertyPayload();
        propertyPayload.put("alias", new Random().nextInt(100000));

        Response response = authorizedPropertyHelper.performCreationPostRequest(propertyPayload);
        //TODO: used ficticious error message, should be updated
        response.then()
                .statusCode(400)
                .body("title", is("Validation Error"))
                .body("detail", is("Invalid attribute type"))
                .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/common/ValidationErrorSchema.json")));
    }

    @Test
    @DisplayName("POST /properties sending only mandatory attributes")
    public void testCreatePropertySendingOnlyAttributes() throws IOException {
        PropertyHelper authorizedPropertyHelper = new PropertyHelper(username, password);

        JSONObject requiredOnlyPayload = createRequiredOnlyPropertyPayload();

        Response response = authorizedPropertyHelper.performCreationPostRequest(requiredOnlyPayload);
        response.then()
                .statusCode(201)
                .body("alias", is(requiredOnlyPayload.getString("alias")))
                .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/property/CreatePropertySchema.json")));
    }

}
