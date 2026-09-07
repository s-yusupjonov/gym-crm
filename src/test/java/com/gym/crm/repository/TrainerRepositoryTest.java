package com.gym.crm.repository;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class TrainerRepositoryTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    private Trainer newTrainer(String username, TrainingType specialization) {
        User user = new User();
        user.setFirstName("Carl");
        user.setLastName("Coach");
        user.setUsername(username);
        user.setPassword("password");
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        return trainer;
    }

    private Trainee newTrainee(String username) {
        User user = new User();
        user.setFirstName("Dana");
        user.setLastName("Dancer");
        user.setUsername(username);
        user.setPassword("password");
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        return trainee;
    }

    private TrainingType newTrainingType(String name) {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName(name);
        return trainingTypeRepository.saveAndFlush(type);
    }

    @Test
    void findByUsernameShouldReturnTrainerWhenExists() {
        TrainingType type = newTrainingType("CARDIO");
        trainerRepository.saveAndFlush(newTrainer("carl.coach", type));

        Optional<Trainer> found = trainerRepository.findByUsername("carl.coach");

        assertTrue(found.isPresent());
        assertEquals("carl.coach", found.get().getUser().getUsername());
    }

    @Test
    void findByUsernameShouldReturnEmptyWhenNotExists() {
        Optional<Trainer> found = trainerRepository.findByUsername("nobody");

        assertFalse(found.isPresent());
    }

    @Test
    void findByUsernamesShouldReturnMatchingTrainers() {
        TrainingType type = newTrainingType("STRENGTH");
        trainerRepository.saveAndFlush(newTrainer("t1", type));
        trainerRepository.saveAndFlush(newTrainer("t2", type));
        trainerRepository.saveAndFlush(newTrainer("t3", type));

        List<Trainer> found = trainerRepository.findByUsernames(List.of("t1", "t3"));

        assertEquals(2, found.size());
    }

    @Test
    void findByUsernamesShouldReturnEmptyListWhenNoneMatch() {
        List<Trainer> found = trainerRepository.findByUsernames(List.of("nobody"));

        assertTrue(found.isEmpty());
    }

    @Test
    void findNotAssignedToTraineeShouldExcludeAssignedTrainers() {
        TrainingType type = newTrainingType("YOGA");
        Trainer assigned = trainerRepository.saveAndFlush(newTrainer("assigned.trainer", type));
        Trainer unassigned = trainerRepository.saveAndFlush(newTrainer("unassigned.trainer", type));

        Trainee trainee = newTrainee("dana.dancer");
        Set<Trainer> trainers = new HashSet<>();
        trainers.add(assigned);
        trainee.setTrainers(trainers);
        traineeRepository.saveAndFlush(trainee);

        List<Trainer> notAssigned = trainerRepository.findNotAssignedToTrainee("dana.dancer");

        assertEquals(1, notAssigned.size());
        assertEquals(unassigned.getUser().getUsername(), notAssigned.get(0).getUser().getUsername());
    }

    @Test
    void findNotAssignedToTraineeShouldReturnAllTrainersWhenNoneAssigned() {
        TrainingType type = newTrainingType("PILATES");
        trainerRepository.saveAndFlush(newTrainer("free.trainer", type));

        Trainee trainee = newTrainee("empty.trainee");
        traineeRepository.saveAndFlush(trainee);

        List<Trainer> notAssigned = trainerRepository.findNotAssignedToTrainee("empty.trainee");

        assertEquals(1, notAssigned.size());
    }
}