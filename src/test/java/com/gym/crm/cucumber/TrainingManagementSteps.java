package com.gym.crm.cucumber;

import com.gym.crm.domain.Training;
import com.gym.crm.repository.TrainingRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TrainingManagementSteps {

    private final TestApiClient apiClient;
    private final ScenarioContext scenarioContext;
    private final TrainingRepository trainingRepository;

    public TrainingManagementSteps(TestApiClient apiClient, ScenarioContext scenarioContext,
                                   TrainingRepository trainingRepository) {
        this.apiClient = apiClient;
        this.scenarioContext = scenarioContext;
        this.trainingRepository = trainingRepository;
    }

    @When("a training is added for the trainee and trainer on a future date with duration {int}")
    public void aTrainingIsAddedForTraineeAndTrainer(int duration) {
        addTraining(scenarioContext.getTraineeUsername(), scenarioContext.getTrainerUsername(),
                uniqueTrainingName(), LocalDate.now().plusDays(7), duration);
    }

    @When("a training is added for trainee username {string} and the registered trainer on a future date "
            + "with duration {int}")
    public void aTrainingIsAddedForMissingTrainee(String traineeUsername, int duration) {
        addTraining(traineeUsername, scenarioContext.getTrainerUsername(),
                uniqueTrainingName(), LocalDate.now().plusDays(7), duration);
    }

    @When("a training is added for the registered trainee and trainer username {string} on a future date "
            + "with duration {int}")
    public void aTrainingIsAddedForMissingTrainer(String trainerUsername, int duration) {
        addTraining(scenarioContext.getTraineeUsername(), trainerUsername,
                uniqueTrainingName(), LocalDate.now().plusDays(7), duration);
    }

    @Given("a training exists for the trainee and trainer on a future date")
    public void aTrainingExistsInTheFuture() {
        createFixtureTrainingAndCaptureId(LocalDate.now().plusDays(7));
    }

    @Given("a training exists for the trainee and trainer on a past date")
    public void aTrainingExistsInThePast() {
        createFixtureTrainingAndCaptureId(LocalDate.now().minusDays(7));
    }

    @When("the most recently created training is cancelled")
    public void theMostRecentlyCreatedTrainingIsCancelled() {
        cancelTraining(scenarioContext.getLastTrainingId());
    }

    @When("training id {long} is cancelled")
    public void trainingIdIsCancelled(long trainingId) {
        cancelTraining(trainingId);
    }

    private void createFixtureTrainingAndCaptureId(LocalDate date) {
        String traineeUsername = scenarioContext.getTraineeUsername();
        String trainingName = uniqueTrainingName();

        addTraining(traineeUsername, scenarioContext.getTrainerUsername(), trainingName, date, 45);
        assertEquals(200, scenarioContext.getLastResponse().getStatusCode().value(),
                "failed to set up training fixture: " + scenarioContext.getLastResponse().getBody());

        Long id = trainingRepository.findTraineeTrainings(traineeUsername, null, null, null, null).stream()
                .filter(t -> trainingName.equals(t.getTrainingName()) && date.equals(t.getTrainingDate()))
                .map(Training::getId)
                .findFirst()
                .orElseGet(() -> fail("could not locate fixture training '" + trainingName + "' after creation"));

        scenarioContext.setLastTrainingId(id);
    }

    private void addTraining(String traineeUsername, String trainerUsername, String trainingName,
                             LocalDate date, int duration) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("traineeUsername", traineeUsername);
        body.put("trainerUsername", trainerUsername);
        body.put("trainingName", trainingName);
        body.put("trainingDate", date.toString());
        body.put("trainingDuration", duration);

        ResponseEntity<String> response = apiClient.post("/api/trainings", body, scenarioContext.getTraineeToken());
        scenarioContext.setLastResponse(response);
    }

    private void cancelTraining(long trainingId) {
        ResponseEntity<String> response =
                apiClient.delete("/api/trainings/" + trainingId, scenarioContext.getTraineeToken());
        scenarioContext.setLastResponse(response);
    }

    private String uniqueTrainingName() {
        return "Fixture Session " + System.nanoTime();
    }
}
