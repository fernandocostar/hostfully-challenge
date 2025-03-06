package com.hostfully.api.tests.property;

import com.hostfully.api.config.BaseTest;
import com.hostfully.api.helpers.PropertyHelper;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.hostfully.api.utils.FileUtils.readJsonFile;
import static com.hostfully.api.utils.requests.PropertyDTOFactory.createValidPropertyPayload;
import static java.util.Optional.empty;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.not;

public class PropertiesListingTests extends BaseTest {

    @Test
    @DisplayName("GET /properties unauthorized access")
    public void testGetPropertiesUnauthorized() {
        PropertyHelper unauthorizedPropertyHelper = new PropertyHelper("invalid", "credentials");

        Response response = unauthorizedPropertyHelper.performGetRequest();
        response.then()
                .statusCode(401);
    }

    @Test
    @DisplayName("GET /properties returns correct properties structure")
    public void testValidGetPropertiesStructure() throws IOException {
        PropertyHelper authorizedPropertyHelper = new PropertyHelper(username, password);

        Response response = authorizedPropertyHelper.performGetRequest();
        response.then()
            .statusCode(200)
            .body("$", not(empty()))
            .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/property/GetPropertiesSchema.json")));
    }

    @Test
    @DisplayName("GET /properties created property is listed")
    public void testCreatedPropertyIsListed() throws IOException {
        PropertyHelper authorizedPropertyHelper = new PropertyHelper(username, password);

        JSONObject createdPropertyPayload = createValidPropertyPayload();
        Response createdPropertyresponse = authorizedPropertyHelper.createValidProperty(createdPropertyPayload);
        String propertyId = createdPropertyresponse.jsonPath().get("id");

        Response listingResponse = authorizedPropertyHelper.performGetRequest();
        listingResponse.then()
                .statusCode(200)
                .body("$", not(empty()))
                .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/property/GetPropertiesSchema.json")))
                .body("id", hasItem(propertyId));
    }

}
