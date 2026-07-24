package com.gym.crm.mapper;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.domain.User;
import com.gym.crm.dto.trainer.TrainerProfileResponse;
import com.gym.crm.dto.trainer.TrainerRegistrationRequest;
import com.gym.crm.dto.trainer.UpdateTrainerRequest;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerMapperTest {

    private TrainingType cardio() {
        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setTrainingTypeName("CARDIO");
        return type;
    }

    @Test
    void toEntityFromRegistrationRequestShouldCopyFieldsAndAssignSpecialization() {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("Carl");
        request.setLastName("Coach");
        request.setSpecializationId(1L);
        TrainingType specialization = cardio();

        Trainer trainer = TrainerMapper.toEntity(request, specialization);

        assertEquals("Carl", trainer.getUser().getFirstName());
        assertEquals("Coach", trainer.getUser().getLastName());
        assertSame(specialization, trainer.getSpecialization());
    }

    @Test
    void toEntityFromUpdateRequestShouldCarryOverCurrentSpecialization() {
        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setFirstName("Carlos");
        request.setLastName("Coach");
        request.setActive(true);
        TrainingType currentSpecialization = cardio();

        Trainer trainer = TrainerMapper.toEntity(request, currentSpecialization);

        assertEquals("Carlos", trainer.getUser().getFirstName());
        assertSame(currentSpecialization, trainer.getSpecialization());
        assertTrue(trainer.getUser().isActive());
    }

    @Test
    void toProfileResponseShouldOmitUsernameAndIncludeTraineesAndSpecialization() {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername("carl.coach");
        user.setFirstName("Carl");
        user.setLastName("Coach");
        user.setActive(true);
        trainer.setUser(user);
        trainer.setSpecialization(cardio());

        Trainee trainee = new Trainee();
        User traineeUser = new User();
        traineeUser.setUsername("john.doe");
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");
        trainee.setUser(traineeUser);
        trainer.setTrainees(Set.of(trainee));

        TrainerProfileResponse response = TrainerMapper.toProfileResponse(trainer);

        assertNull(response.getUsername());
        assertEquals("Carl", response.getFirstName());
        assertEquals("CARDIO", response.getSpecialization().getTrainingTypeName());
        assertTrue(response.isActive());
        assertEquals(1, response.getTrainees().size());
        assertEquals("john.doe", response.getTrainees().get(0).getUsername());
    }

    @Test
    void toUpdateResponseShouldIncludeUsername() {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername("carl.coach");
        user.setFirstName("Carl");
        user.setLastName("Coach");
        trainer.setUser(user);
        trainer.setSpecialization(cardio());
        trainer.setTrainees(Set.of());

        TrainerProfileResponse response = TrainerMapper.toUpdateResponse(trainer);

        assertEquals("carl.coach", response.getUsername());
    }
}