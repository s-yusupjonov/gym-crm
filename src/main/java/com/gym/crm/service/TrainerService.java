package com.gym.crm.service;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.User;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    private final TrainerDao trainerDao;
    private UserProfileService userProfileService;

    @Autowired
    public TrainerService(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setUserProfileService(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Transactional
    public Trainer createTrainerProfile(Trainer trainer) {
        validateForCreate(trainer);

        User user = trainer.getUser();
        user.setUsername(userProfileService.generateUsername(user.getFirstName(), user.getLastName()));
        user.setPassword(userProfileService.generatePassword());
        user.setActive(true);

        Trainer saved = trainerDao.save(trainer);
        log.info("Created trainer profile: username={}", user.getUsername());

        return saved;
    }

    @Transactional
    public Trainer updateTrainerProfile(String username, Trainer updates) {
        validateForUpdate(updates);

        Trainer existing = getByUsername(username);
        existing.getUser().setFirstName(updates.getUser().getFirstName());
        existing.getUser().setLastName(updates.getUser().getLastName());
        existing.setSpecialization(updates.getSpecialization());

        log.info("Updated trainer profile: username={}", username);

        return existing;
    }

    @Transactional
    public void changePassword(String username, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new ValidationException("New password must not be blank");
        }

        Trainer trainer = getByUsername(username);
        trainer.getUser().setPassword(newPassword);

        log.info("Changed password for trainer username={}", username);
    }

    @Transactional
    public void setActive(String username, boolean active) {
        Trainer trainer = getByUsername(username);
        trainer.getUser().setActive(active);

        log.info("Trainer username={} active flag set to {}", username, active);
    }

    @Transactional(readOnly = true)
    public Trainer getByUsername(String username) {
        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));
    }

    @Transactional(readOnly = true)
    public List<Trainer> getAll() {
        return trainerDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername) {
        return trainerDao.findNotAssignedToTrainee(traineeUsername);
    }

    private void validateForCreate(Trainer trainer) {
        if (trainer.getUser() == null) {
            throw new ValidationException("User details are required");
        }
        if (isBlank(trainer.getUser().getFirstName())) {
            throw new ValidationException("First name is required");
        }
        if (isBlank(trainer.getUser().getLastName())) {
            throw new ValidationException("Last name is required");
        }
        if (trainer.getSpecialization() == null) {
            throw new ValidationException("Specialization is required");
        }
    }

    private void validateForUpdate(Trainer trainer) {
        if (trainer.getUser() == null || isBlank(trainer.getUser().getFirstName())
                || isBlank(trainer.getUser().getLastName())) {
            throw new ValidationException("First name and last name are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}