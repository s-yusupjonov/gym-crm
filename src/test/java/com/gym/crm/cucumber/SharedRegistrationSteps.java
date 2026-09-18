package com.gym.crm.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SharedRegistrationSteps {

    private final TestApiClient apiClient;
    private final ScenarioContext scenarioContext;
    private final ObjectMapper objectMapper;

    public SharedRegistrationSteps(TestApiClient apiClient, ScenarioContext scenarioContext,
                                   ObjectMapper objectMapper) {
        this.apiClient = apiClient;
        this.scenarioContext = scenarioContext;
        this.objectMapper = objectMapper;
    }

    @Given("a registered trainee exists with first name {string} and last name {string}")
    public void aRegisteredTraineeExists(String firstName, String lastName) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("firstName", firstName);
        body.put("lastName", lastName);

        ResponseEntity<String> response = apiClient.post("/api/trainees", body, null);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value(),
                "failed to set up trainee fixture: " + response.getBody());

        JsonNode json = objectMapper.readTree(response.getBody());
        scenarioContext.setTraineeUsername(json.get("username").asText());
        scenarioContext.setTraineePassword(json.get("password").asText());
    }

    @Given("a registered trainer exists with first name {string} and last name {string} and specialization id {int}")
    public void aRegisteredTrainerExists(String firstName, String lastName, int specializationId) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        body.put("specializationId", specializationId);

        ResponseEntity<String> response = apiClient.post("/api/trainers", body, null);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value(),
                "failed to set up trainer fixture: " + response.getBody());

        JsonNode json = objectMapper.readTree(response.getBody());
        scenarioContext.setTrainerUsername(json.get("username").asText());
        scenarioContext.setTrainerPassword(json.get("password").asText());
    }

    @Given("the trainee is authenticated")
    public void theTraineeIsAuthenticated() throws Exception {
        scenarioContext.setTraineeToken(
                login(scenarioContext.getTraineeUsername(), scenarioContext.getTraineePassword()));
    }

    @Given("the trainer is authenticated")
    public void theTrainerIsAuthenticated() throws Exception {
        scenarioContext.setTrainerToken(
                login(scenarioContext.getTrainerUsername(), scenarioContext.getTrainerPassword()));
    }

    private String login(String username, String password) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("username", username);
        body.put("password", password);

        ResponseEntity<String> response = apiClient.post("/api/login", body, null);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value(),
                "failed to authenticate fixture user '" + username + "': " + response.getBody());

        return objectMapper.readTree(response.getBody()).get("token").asText();
    }
}
