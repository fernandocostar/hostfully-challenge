package com.hostfully.api.helpers;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import static com.hostfully.api.utils.DateUtils.castToDateList;
import static com.hostfully.api.utils.FileUtils.readJsonFile;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class BookingHelper {

    private final String username;
    private final String password;
    private final String POST_BOOKINGS_ENDPOINT = "/bookings"; //TODO: get from centralized endpoints file

    public BookingHelper(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Response createValidBooking(JSONObject requestPayload) throws IOException {
        List<Integer> expectedStartDate = castToDateList(requestPayload.get("startDate").toString());
        List<Integer> expectedEndDate = castToDateList(requestPayload.get("endDate").toString());

        Response response = performCreationPostRequest(requestPayload);
        validateResponse(response, requestPayload, expectedStartDate, expectedEndDate);

        return response;
    }

    public Response performCreationPostRequest(JSONObject requestPayload) {
        return authenticateRequest()
                    .body(requestPayload.toString())
                .when()
                    .post(POST_BOOKINGS_ENDPOINT)
                .then()
                    .extract().response();
    }

    private io.restassured.specification.RequestSpecification authenticateRequest() {
        return given()
                .auth()
                .preemptive()
                .basic(username, password);
    }

    private void validateResponse(Response response, JSONObject requestPayload,
                                  List<Integer> expectedStartDate, List<Integer> expectedEndDate) throws IOException {
        response.then()
                .statusCode(201)
                .body(JsonSchemaValidator.matchesJsonSchema(readJsonFile("src/test/resources/schemas/booking/BookingCreationSchema.json")))
                .body("status", is(requestPayload.get("status")))
                .body("propertyId", is(requestPayload.get("propertyId")))
                .body("startDate", is(expectedStartDate))
                .body("endDate", is(expectedEndDate));
    }
}
