package com.hostfully.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.time.LocalDateTime;

public class PrettyLoggingFilter implements Filter {

    private static final int BODY_LIMIT = 1500;

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        System.out.println("\n┌────────────────────────────────────────────────");
        System.out.println("│ REQUEST: " + LocalDateTime.now());
        System.out.println("│ Method: " + requestSpec.getMethod());
        System.out.println("│ URL: " + requestSpec.getURI());
        System.out.println("│ Headers: ");
        requestSpec.getHeaders().forEach(header ->
                System.out.println("│   " + header.getName() + ": " + header.getValue()));

        if (requestSpec.getBody() != null) {
            System.out.println("│ Body: ");
            logLimitedBody(requestSpec.getBody().toString());
        }

        Response response = ctx.next(requestSpec, responseSpec);

        System.out.println("│");
        System.out.println("│ RESPONSE: " + LocalDateTime.now());
        System.out.println("│ Status: " + response.getStatusCode() + " " + response.getStatusLine());
        System.out.println("│ Time: " + response.getTime() + "ms");
        System.out.println("│ Headers: ");
        response.getHeaders().forEach(header ->
                System.out.println("│   " + header.getName() + ": " + header.getValue()));

        try {
            String responseBody = response.getBody().asString();
            if (responseBody != null && !responseBody.isEmpty()) {
                System.out.println("│ Body: ");
                if (response.getContentType() != null && response.getContentType().contains("json")) {
                    ObjectMapper mapper = new ObjectMapper();
                    Object json = mapper.readValue(responseBody, Object.class);
                    String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
                    logLimitedBody(indented);
                } else {
                    logLimitedBody(responseBody);
                }
            }
        } catch (Exception e) {
            System.out.println("│ Body: (Could not parse) " + response.getBody().asString());
        }

        System.out.println("└────────────────────────────────────────────────\n");

        return response;
    }

    /**
     * Logs a truncated version of the body if it's too long.
     */
    private void logLimitedBody(String body) {
        if (body.length() > BODY_LIMIT) {
            System.out.println("│   " + body.substring(0, BODY_LIMIT).replace("\n", "\n│   ") + "...");
            System.out.println("│   [TRUNCATED: " + body.length() + " characters]");
        } else {
            System.out.println("│   " + body.replace("\n", "\n│   "));
        }
    }
}
