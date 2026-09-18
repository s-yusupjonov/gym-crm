package com.gym.crm.cucumber;

import io.cucumber.java.en.When;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public class TrainerRegistrationSteps {

    private final TestApiClient apiClient;
    private final ScenarioContext scenarioContext;

    public TrainerRegistrationSteps(TestApiClient apiClient, ScenarioContext scenarioContext) {
        this.apiClient = apiClient;
        this.scenarioContext = scenarioContext;
    }

    @When("a trainer registers with first name {string} and last name {string} and specialization id {int}")
    public void aTrainerRegisters(String firstName, String lastName, int specializationId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        body.put("specializationId", specializationId);

        ResponseEntity<String> response = apiClient.post("/api/trainers", body, null);
        scenarioContext.setLastResponse(response);
    }
}
