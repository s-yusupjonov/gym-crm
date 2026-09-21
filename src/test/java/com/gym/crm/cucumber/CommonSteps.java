package com.gym.crm.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommonSteps {

    private final ScenarioContext scenarioContext;
    private final ObjectMapper objectMapper;

    public CommonSteps(ScenarioContext scenarioContext, ObjectMapper objectMapper) {
        this.scenarioContext = scenarioContext;
        this.objectMapper = objectMapper;
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertNotNull(scenarioContext.getLastResponse(), "no HTTP response was recorded for this scenario");
        assertEquals(expectedStatus, scenarioContext.getLastResponse().getStatusCode().value(),
                "unexpected status; body was: " + scenarioContext.getLastResponse().getBody());
    }

    @Then("the response should contain a field error for {string}")
    public void theResponseShouldContainAFieldErrorFor(String field) throws Exception {
        JsonNode body = objectMapper.readTree(scenarioContext.getLastResponse().getBody());
        JsonNode fieldErrors = body.get("fieldErrors");
        assertNotNull(fieldErrors, "expected a 'fieldErrors' array in error response: " + body);

        boolean found = false;
        for (JsonNode fieldError : fieldErrors) {
            if (field.equals(fieldError.get("field").asText())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "expected a field error for '" + field + "' but got: " + fieldErrors);
    }
}
