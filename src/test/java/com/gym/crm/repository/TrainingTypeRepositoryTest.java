package com.gym.crm.repository;

import com.gym.crm.domain.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class TrainingTypeRepositoryTest {

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Test
    void saveAndFindByIdShouldRoundTrip() {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("CARDIO");

        TrainingType saved = trainingTypeRepository.saveAndFlush(type);
        Optional<TrainingType> found = trainingTypeRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("CARDIO", found.get().getTrainingTypeName());
    }

    @Test
    void findAllShouldReturnEveryPersistedTrainingType() {
        TrainingType first = new TrainingType();
        first.setTrainingTypeName("STRENGTH");
        TrainingType second = new TrainingType();
        second.setTrainingTypeName("YOGA");

        trainingTypeRepository.saveAndFlush(first);
        trainingTypeRepository.saveAndFlush(second);

        List<TrainingType> all = trainingTypeRepository.findAll();

        assertEquals(2, all.size());
    }
}