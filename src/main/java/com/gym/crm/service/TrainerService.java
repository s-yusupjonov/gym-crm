package com.gym.crm.service;

import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.domain.User;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.repository.TrainerRepository;
import com.gym.crm.repository.TrainingTypeRepository;
import com.gym.crm.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    private final TrainerRepository trainerRepository;
    private final UserProfileService userProfileService;
    private final TrainingTypeRepository trainingTypeRepository;
    private final PasswordEncoder passwordEncoder;

    public TrainerService(TrainerRepository trainerRepository, UserProfileService userProfileService,
                          TrainingTypeRepository trainingTypeRepository, PasswordEncoder passwordEncoder) {
        this.trainerRepository = trainerRepository;
        this.userProfileService = userProfileService;
        this.trainingTypeRepository = trainingTypeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Trainer createTrainerProfile(Trainer trainer) {
        validateForCreate(trainer);

        User user = trainer.getUser();
        user.setUsername(userProfileService.generateUsername(user.getFirstName(), user.getLastName()));
        String rawPassword = userProfileService.generatePassword();
        user.setRawPassword(rawPassword);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setActive(true);

        Trainer saved = trainerRepository.save(trainer);
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
        existing.getUser().setActive(updates.getUser().isActive());

        log.info("Updated trainer profile: username={}", username);

        return existing;
    }

    @Transactional(readOnly = true)
    public TrainingType getSpecializationById(Long trainingTypeId) {
        if (trainingTypeId == null) {
            throw new ValidationException("Specialization is required");
        }
        return trainingTypeRepository.findById(trainingTypeId)
                .orElseThrow(() -> new ValidationException("Invalid specialization id: " + trainingTypeId));
    }

    @Transactional
    public void setActive(String username, boolean active) {
        Trainer trainer = getByUsername(username);
        trainer.getUser().setActive(active);

        log.info("Trainer username={} active flag set to {}", username, active);
    }

    @Transactional(readOnly = true)
    public Trainer getByUsername(String username) {
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));
    }

    @Transactional(readOnly = true)
    public List<Trainer> getAll() {
        return trainerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername) {
        return trainerRepository.findNotAssignedToTrainee(traineeUsername);
    }

    private void validateForCreate(Trainer trainer) {
        if (trainer.getUser() == null) {
            throw new ValidationException("User details are required");
        }
        if (ValidationUtils.isBlank(trainer.getUser().getFirstName())) {
            throw new ValidationException("First name is required");
        }
        if (ValidationUtils.isBlank(trainer.getUser().getLastName())) {
            throw new ValidationException("Last name is required");
        }
        if (trainer.getSpecialization() == null) {
            throw new ValidationException("Specialization is required");
        }
    }

    private void validateForUpdate(Trainer trainer) {
        if (trainer.getUser() == null || ValidationUtils.isBlank(trainer.getUser().getFirstName())
                || ValidationUtils.isBlank(trainer.getUser().getLastName())) {
            throw new ValidationException("First name and last name are required");
        }
    }
}