package com.hostfully.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.time.LocalDateTime;
import io.restassured.filter.Filter;

public class PrettyLoggingFilter implements Filter {
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
            System.out.println("│   " + requestSpec.getBody().toString().replace("\n", "\n│   "));
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
                if (response.getContentType().contains("json")) {
                    ObjectMapper mapper = new ObjectMapper();
                    Object json = mapper.readValue(responseBody, Object.class);
                    String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
                    System.out.println("│   " + indented.replace("\n", "\n│   "));
                } else {
                    System.out.println("│   " + responseBody.replace("\n", "\n│   "));
                }
            }
        } catch (Exception e) {
            System.out.println("│ Body: (Could not parse) " + response.getBody().asString());
        }

        System.out.println("└────────────────────────────────────────────────\n");

        return response;
    }
}
