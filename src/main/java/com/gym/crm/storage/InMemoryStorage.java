package com.gym.crm.storage;
import com.gym.crm.domain.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.Map;
@Component
public class InMemoryStorage {
    private final Map<Long, Trainee> traineeStorage;
    private final Map<Long, Trainer> trainerStorage;
    private final Map<Long, Training> trainingStorage;
    public InMemoryStorage(@Qualifier("traineeStorage") Map<Long, Trainee> traineeStorage,
                           @Qualifier("trainerStorage") Map<Long, Trainer> trainerStorage,
                           @Qualifier("trainingStorage") Map<Long, Training> trainingStorage) {
        this.traineeStorage = traineeStorage;
        this.trainerStorage = trainerStorage;
        this.trainingStorage = trainingStorage;
    }
    public Map<Long, Trainee> getTraineeStorage() { return traineeStorage; }
    public Map<Long, Trainer> getTrainerStorage() { return trainerStorage; }
    public Map<Long, Training> getTrainingStorage() { return trainingStorage; }
}
