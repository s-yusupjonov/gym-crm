package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.User;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final UserProfileService userProfileService;

    public TraineeService(TraineeDao traineeDao, TrainerDao trainerDao, UserProfileService userProfileService) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.userProfileService = userProfileService;
    }

    @Transactional
    public Trainee createTraineeProfile(Trainee trainee) {
        validateForCreate(trainee);

        User user = trainee.getUser();
        user.setUsername(userProfileService.generateUsername(user.getFirstName(), user.getLastName()));
        user.setPassword(userProfileService.generatePassword());
        user.setActive(true);

        Trainee saved = traineeDao.save(trainee);
        log.info("Created trainee profile: username={}", user.getUsername());

        return saved;
    }

    @Transactional
    public Trainee updateTraineeProfile(String username, Trainee updates) {
        validateForUpdate(updates);

        Trainee existing = getByUsername(username);
        existing.getUser().setFirstName(updates.getUser().getFirstName());
        existing.getUser().setLastName(updates.getUser().getLastName());
        existing.setDateOfBirth(updates.getDateOfBirth());
        existing.setAddress(updates.getAddress());
        existing.getUser().setActive(updates.getUser().isActive());

        log.info("Updated trainee profile: username={}", username);

        return existing;
    }

    @Transactional
    public void changePassword(String username, String newPassword) {
        ValidationUtils.requireNonBlank(newPassword, "New password must not be blank");

        Trainee trainee = getByUsername(username);
        trainee.getUser().setPassword(newPassword);

        log.info("Changed password for trainee username={}", username);
    }

    @Transactional
    public void setActive(String username, boolean active) {
        Trainee trainee = getByUsername(username);
        trainee.getUser().setActive(active);

        log.info("Trainee username={} active flag set to {}", username, active);
    }

    @Transactional
    public void deleteByUsername(String username) {
        Trainee trainee = getByUsername(username);
        traineeDao.delete(trainee);

        log.info("Deleted trainee profile: username={}", username);
    }

    @Transactional(readOnly = true)
    public Trainee getByUsername(String username) {
        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));
    }

    @Transactional(readOnly = true)
    public List<Trainee> getAll() {
        return traineeDao.findAll();
    }

    @Transactional
    public List<Trainer> updateTrainersList(String traineeUsername, List<String> trainerUsernames) {
        Trainee trainee = getByUsername(traineeUsername);
        List<Trainer> trainers = trainerDao.findByUsernames(trainerUsernames);

        if (trainers.size() != trainerUsernames.size()) {
            throw new ValidationException("One or more trainer usernames do not exist");
        }

        Set<Trainer> updated = new HashSet<>(trainers);
        trainee.setTrainers(updated);

        log.info("Updated trainers list for trainee username={}", traineeUsername);

        return new ArrayList<>(updated);
    }

    private void validateForCreate(Trainee trainee) {
        if (trainee.getUser() == null) {
            throw new ValidationException("User details are required");
        }
        if (ValidationUtils.isBlank(trainee.getUser().getFirstName())) {
            throw new ValidationException("First name is required");
        }
        if (ValidationUtils.isBlank(trainee.getUser().getLastName())) {
            throw new ValidationException("Last name is required");
        }
    }

    private void validateForUpdate(Trainee trainee) {
        if (trainee.getUser() == null || ValidationUtils.isBlank(trainee.getUser().getFirstName())
                || ValidationUtils.isBlank(trainee.getUser().getLastName())) {
            throw new ValidationException("First name and last name are required");
        }
    }
}