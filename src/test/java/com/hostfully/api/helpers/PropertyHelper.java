package com.hostfully.api.helpers;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;

import static com.hostfully.api.utils.FileUtils.readJsonFile;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class PropertyHelper {

    private final String username;
    private final String password;

    //TODO: get from centralized endpoints file
    private final String POST_PROPERTIES_ENDPOINT = "/properties";
    private final String GET_PROPERTY_BY_ID_ENDPOINT = "/properties/{propertyId}";
    private final String GET_PROPERTIES_ENDPOINT = "/properties";

    public PropertyHelper(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Response createValidProperty(JSONObject requestPayload) {
        Response response = performCreationPostRequest(requestPayload);
        validateResponse(response, requestPayload);

        return response;
    }

    public Response performGetRequest() {
        return authenticateRequest()
                .when()
                .get(GET_PROPERTIES_ENDPOINT)
                .then()
                .extract().response();
    }

    public Response performGetByIdRequest(String propertyId) {
        return authenticateRequest()
                .when()
                .get(GET_PROPERTY_BY_ID_ENDPOINT.replace("{propertyId}", propertyId))
                .then()
                .extract().response();
    }

    public Response performCreationPostRequest(JSONObject requestPayload) {
        return authenticateRequest()
                .body(requestPayload.toString())
                .when()
                .post(POST_PROPERTIES_ENDPOINT)
                .then()
                .extract().response();
    }

    private io.restassured.specification.RequestSpecification authenticateRequest() {
        return given()
                .auth()
                .preemptive()
                .basic(username, password);
    }

    private void validateResponse(Response response, JSONObject requestPayload) {
        response.then()
                .statusCode(201);
    }
}
