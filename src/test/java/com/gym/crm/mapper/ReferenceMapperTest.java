package com.gym.crm.mapper;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.domain.User;
import com.gym.crm.dto.common.TraineeShortDto;
import com.gym.crm.dto.common.TrainerShortDto;
import com.gym.crm.dto.common.TrainingTypeDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReferenceMapperTest {

    @Test
    void toTrainingTypeDtoShouldCopyIdAndName() {
        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setTrainingTypeName("CARDIO");

        TrainingTypeDto dto = ReferenceMapper.toTrainingTypeDto(type);

        assertEquals(1L, dto.getId());
        assertEquals("CARDIO", dto.getTrainingTypeName());
    }

    @Test
    void toTrainingTypeDtoShouldReturnNullForNullInput() {
        assertNull(ReferenceMapper.toTrainingTypeDto(null));
    }

    @Test
    void toTrainerShortDtoShouldCopyUserAndSpecializationFields() {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername("carl.coach");
        user.setFirstName("Carl");
        user.setLastName("Coach");
        trainer.setUser(user);
        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setTrainingTypeName("CARDIO");
        trainer.setSpecialization(type);

        TrainerShortDto dto = ReferenceMapper.toTrainerShortDto(trainer);

        assertEquals("carl.coach", dto.getUsername());
        assertEquals("Carl", dto.getFirstName());
        assertEquals("Coach", dto.getLastName());
        assertEquals("CARDIO", dto.getSpecialization().getTrainingTypeName());
    }

    @Test
    void toTrainerShortDtoListShouldMapEveryElement() {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername("carl.coach");
        user.setFirstName("Carl");
        user.setLastName("Coach");
        trainer.setUser(user);
        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setTrainingTypeName("CARDIO");
        trainer.setSpecialization(type);

        List<TrainerShortDto> dtos = ReferenceMapper.toTrainerShortDtoList(List.of(trainer));

        assertEquals(1, dtos.size());
        assertEquals("carl.coach", dtos.get(0).getUsername());
    }

    @Test
    void toTraineeShortDtoShouldCopyUserFields() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setUsername("john.doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        trainee.setUser(user);

        TraineeShortDto dto = ReferenceMapper.toTraineeShortDto(trainee);

        assertEquals("john.doe", dto.getUsername());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
    }

    @Test
    void toTraineeShortDtoListShouldMapEveryElement() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setUsername("john.doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        trainee.setUser(user);

        List<TraineeShortDto> dtos = ReferenceMapper.toTraineeShortDtoList(List.of(trainee));

        assertEquals(1, dtos.size());
        assertEquals("john.doe", dtos.get(0).getUsername());
    }
}