package com.hostfully.api.tests.property;

import com.hostfully.api.config.BaseTest;
import com.hostfully.api.helpers.PropertyHelper;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.hostfully.api.utils.FileUtils.readJsonFile;
import static java.util.Optional.empty;
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

    //TODO: CREATED PROPERTY IS LISTED
}
