package com.hostfully.api.tests.property;

import com.hostfully.api.config.BaseTest;
import com.hostfully.api.helpers.PropertyHelper;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.hostfully.api.utils.requests.PropertyDTOFactory.createValidPropertyPayload;
import static org.hamcrest.CoreMatchers.is;

public class PropertySearchTests extends BaseTest {

    @Test
    @DisplayName("GET /properties/{id} searching fails for property by ID without authorization")
    public void testErrorGetPropertyByIdUnauthorized() {
        PropertyHelper unauthorizedPropertyHelper = new PropertyHelper("invalid", "credentials");

        Response response = unauthorizedPropertyHelper.performGetByIdRequest("7a58559f-1a64-4c24-a4f7-28a6b81302cc");
        response.then()
                .statusCode(401)
                .body("error", is("Unauthorized"))
                .body("exception", is("Bad credentials"))
                .body("message", is("Error while authenticating your access"))
                .body("path", is("/properties/7a58559f-1a64-4c24-a4f7-28a6b81302cc"));
    }

    @Test
    @DisplayName("GET /properties/{id} fail searching for invalid property by ID")
    public void testErrorWhenGetPropertyByInvalidId() {
        PropertyHelper authorizedPropertyHelper = new PropertyHelper(username, password);

        Response response = authorizedPropertyHelper.performGetByIdRequest("abc");
        response.then()
                .statusCode(400)
                .body("title", is("Bad Request"))
                .body("detail", is("Failed to convert 'propertyId' with value: 'abc'"));
    }

    @Test
    @DisplayName("GET /properties/{id} fail searching for non-existing property by ID")
    public void testErrorWhenGetPropertyByNonExistingId() {
        PropertyHelper authorizedPropertyHelper = new PropertyHelper(username, password);

        String randomUUID = java.util.UUID.randomUUID().toString();

        Response response = authorizedPropertyHelper.performGetByIdRequest(randomUUID);
        response.then()
                .statusCode(204);
    }

    //TEST DEPENDS ON PROPERTY CREATION FEATURE

    @Test
    @DisplayName("GET /properties/{id} search for property by ID successfully")
    public void testGetPropertyByIdSuccessfully() {
        PropertyHelper authorizedPropertyHelper = new PropertyHelper(username, password);

        JSONObject propertyCreationPayload = createValidPropertyPayload();
        Response createdPropertyResponse = authorizedPropertyHelper.createValidProperty(propertyCreationPayload);
        String propertyId = createdPropertyResponse.jsonPath().get("id");
        String propertyAlias = createdPropertyResponse.jsonPath().get("alias");

        Response response = authorizedPropertyHelper.performGetByIdRequest(propertyId);
        response.then()
                .statusCode(200)
                .body("id", is(propertyId))
                .body("alias", is(propertyAlias)); //TODO: validate schema once country code bug is fixed
    }

}
