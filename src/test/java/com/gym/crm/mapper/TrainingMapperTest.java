package com.gym.crm.mapper;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.Training;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.domain.User;
import com.gym.crm.dto.training.AddTrainingRequest;
import com.gym.crm.dto.training.TraineeTrainingResponse;
import com.gym.crm.dto.training.TrainerTrainingResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class TrainingMapperTest {

    private TrainingType cardio() {
        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setTrainingTypeName("CARDIO");
        return type;
    }

    private Trainee trainee() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setUsername("john.doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        trainee.setUser(user);
        return trainee;
    }

    private Trainer trainer() {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername("carl.coach");
        user.setFirstName("Carl");
        user.setLastName("Coach");
        trainer.setUser(user);
        trainer.setSpecialization(cardio());
        return trainer;
    }

    @Test
    void toEntityShouldDeriveTrainingTypeFromTrainerSpecialization() {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("john.doe");
        request.setTrainerUsername("carl.coach");
        request.setTrainingName("Morning Cardio");
        request.setTrainingDate(LocalDate.of(2026, 8, 1));
        request.setTrainingDuration(60);

        Trainee trainee = trainee();
        Trainer trainer = trainer();

        Training training = TrainingMapper.toEntity(request, trainee, trainer);

        assertSame(trainee, training.getTrainee());
        assertSame(trainer, training.getTrainer());
        assertEquals("Morning Cardio", training.getTrainingName());
        assertEquals(LocalDate.of(2026, 8, 1), training.getTrainingDate());
        assertEquals(60, training.getTrainingDuration());
        assertSame(trainer.getSpecialization(), training.getTrainingType());
    }

    @Test
    void toTraineeTrainingResponseShouldIncludeTrainerFullName() {
        Training training = new Training();
        training.setTrainee(trainee());
        training.setTrainer(trainer());
        training.setTrainingName("Morning Cardio");
        training.setTrainingDate(LocalDate.of(2026, 8, 1));
        training.setTrainingDuration(60);
        training.setTrainingType(cardio());

        TraineeTrainingResponse response = TrainingMapper.toTraineeTrainingResponse(training);

        assertEquals("Morning Cardio", response.getTrainingName());
        assertEquals("CARDIO", response.getTrainingType());
        assertEquals(60, response.getTrainingDuration());
        assertEquals("Carl Coach", response.getTrainerName());
    }

    @Test
    void toTraineeTrainingResponseListShouldMapEveryElement() {
        Training training = new Training();
        training.setTrainee(trainee());
        training.setTrainer(trainer());
        training.setTrainingName("Morning Cardio");
        training.setTrainingDate(LocalDate.of(2026, 8, 1));
        training.setTrainingDuration(60);
        training.setTrainingType(cardio());

        List<TraineeTrainingResponse> responses = TrainingMapper.toTraineeTrainingResponseList(List.of(training));

        assertEquals(1, responses.size());
        assertEquals("Morning Cardio", responses.get(0).getTrainingName());
    }

    @Test
    void toTrainerTrainingResponseShouldIncludeTraineeFullName() {
        Training training = new Training();
        training.setTrainee(trainee());
        training.setTrainer(trainer());
        training.setTrainingName("Morning Cardio");
        training.setTrainingDate(LocalDate.of(2026, 8, 1));
        training.setTrainingDuration(60);
        training.setTrainingType(cardio());

        TrainerTrainingResponse response = TrainingMapper.toTrainerTrainingResponse(training);

        assertEquals("Morning Cardio", response.getTrainingName());
        assertEquals("John Doe", response.getTraineeName());
    }

    @Test
    void toTrainerTrainingResponseListShouldMapEveryElement() {
        Training training = new Training();
        training.setTrainee(trainee());
        training.setTrainer(trainer());
        training.setTrainingName("Morning Cardio");
        training.setTrainingDate(LocalDate.of(2026, 8, 1));
        training.setTrainingDuration(60);
        training.setTrainingType(cardio());

        List<TrainerTrainingResponse> responses = TrainingMapper.toTrainerTrainingResponseList(List.of(training));

        assertEquals(1, responses.size());
        assertEquals("John Doe", responses.get(0).getTraineeName());
    }
}