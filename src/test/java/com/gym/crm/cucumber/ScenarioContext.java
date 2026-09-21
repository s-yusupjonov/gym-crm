package com.gym.crm.cucumber;

import io.cucumber.spring.ScenarioScope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class ScenarioContext {

    private ResponseEntity<String> lastResponse;

    private String traineeUsername;
    private String traineePassword;
    private String traineeToken;

    private String trainerUsername;
    private String trainerPassword;
    private String trainerToken;

    private Long lastTrainingId;

    public ResponseEntity<String> getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(ResponseEntity<String> lastResponse) {
        this.lastResponse = lastResponse;
    }

    public String getTraineeUsername() {
        return traineeUsername;
    }

    public void setTraineeUsername(String traineeUsername) {
        this.traineeUsername = traineeUsername;
    }

    public String getTraineePassword() {
        return traineePassword;
    }

    public void setTraineePassword(String traineePassword) {
        this.traineePassword = traineePassword;
    }

    public String getTraineeToken() {
        return traineeToken;
    }

    public void setTraineeToken(String traineeToken) {
        this.traineeToken = traineeToken;
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    public String getTrainerPassword() {
        return trainerPassword;
    }

    public void setTrainerPassword(String trainerPassword) {
        this.trainerPassword = trainerPassword;
    }

    public String getTrainerToken() {
        return trainerToken;
    }

    public void setTrainerToken(String trainerToken) {
        this.trainerToken = trainerToken;
    }

    public Long getLastTrainingId() {
        return lastTrainingId;
    }

    public void setLastTrainingId(Long lastTrainingId) {
        this.lastTrainingId = lastTrainingId;
    }
}
