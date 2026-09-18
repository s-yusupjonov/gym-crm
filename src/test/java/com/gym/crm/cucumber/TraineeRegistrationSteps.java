package com.gym.crm.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TraineeRegistrationSteps {

    private final TestApiClient apiClient;
    private final ScenarioContext scenarioContext;
    private final ObjectMapper objectMapper;

    public TraineeRegistrationSteps(TestApiClient apiClient, ScenarioContext scenarioContext,
                                    ObjectMapper objectMapper) {
        this.apiClient = apiClient;
        this.scenarioContext = scenarioContext;
        this.objectMapper = objectMapper;
    }

    @When("a trainee registers with first name {string} and last name {string}")
    public void aTraineeRegistersWithFirstNameAndLastName(String firstName, String lastName) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("firstName", firstName);
        body.put("lastName", lastName);

        ResponseEntity<String> response = apiClient.post("/api/trainees", body, null);
        scenarioContext.setLastResponse(response);
    }

    @Then("the registration response should contain a generated username and password")
    public void theRegistrationResponseShouldContainCredentials() throws Exception {
        JsonNode body = objectMapper.readTree(scenarioContext.getLastResponse().getBody());

        assertTrue(body.hasNonNull("username"), "expected a 'username' field in: " + body);
        assertFalse(body.get("username").asText().isBlank(), "expected a non-blank username");

        assertTrue(body.hasNonNull("password"), "expected a 'password' field in: " + body);
        assertFalse(body.get("password").asText().isBlank(), "expected a non-blank password");
    }
}
