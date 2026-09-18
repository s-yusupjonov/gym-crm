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

public class AuthenticationSteps {

    private final TestApiClient apiClient;
    private final ScenarioContext scenarioContext;
    private final ObjectMapper objectMapper;

    public AuthenticationSteps(TestApiClient apiClient, ScenarioContext scenarioContext,
                               ObjectMapper objectMapper) {
        this.apiClient = apiClient;
        this.scenarioContext = scenarioContext;
        this.objectMapper = objectMapper;
    }

    @When("the trainee logs in with their generated credentials")
    public void theTraineeLogsInWithGeneratedCredentials() {
        login(scenarioContext.getTraineeUsername(), scenarioContext.getTraineePassword());
    }

    @When("the trainee logs in with their username and password {string}")
    public void theTraineeLogsInWithWrongPassword(String password) {
        login(scenarioContext.getTraineeUsername(), password);
    }

    @When("a user logs in with username {string} and password {string}")
    public void aUserLogsInWithUsernameAndPassword(String username, String password) {
        login(username, password);
    }

    private void login(String username, String password) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("username", username);
        body.put("password", password);

        ResponseEntity<String> response = apiClient.post("/api/login", body, null);
        scenarioContext.setLastResponse(response);
    }

    @Then("a JWT token is returned")
    public void aJwtTokenIsReturned() throws Exception {
        JsonNode body = objectMapper.readTree(scenarioContext.getLastResponse().getBody());
        assertTrue(body.hasNonNull("token"), "expected a 'token' field in: " + body);
        assertFalse(body.get("token").asText().isBlank(), "expected a non-blank token");
    }

    @When("an unauthenticated request is made to get the trainee's own profile")
    public void anUnauthenticatedRequestIsMadeToGetTraineeProfile() {
        ResponseEntity<String> response =
                apiClient.get("/api/trainees/" + scenarioContext.getTraineeUsername(), null);
        scenarioContext.setLastResponse(response);
    }

    @When("the trainee requests their own profile using the issued token")
    public void theTraineeRequestsTheirOwnProfileUsingTheIssuedToken() throws Exception {
        String token = objectMapper.readTree(scenarioContext.getLastResponse().getBody()).get("token").asText();
        ResponseEntity<String> response =
                apiClient.get("/api/trainees/" + scenarioContext.getTraineeUsername(), token);
        scenarioContext.setLastResponse(response);
    }
}
