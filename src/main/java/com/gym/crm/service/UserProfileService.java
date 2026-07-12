package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserProfileService {

    private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;

    private final SecureRandom random = new SecureRandom();

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Autowired
    public void setTraineeDao(TraineeDao d) {
        this.traineeDao = d;
    }

    @Autowired
    public void setTrainerDao(TrainerDao d) {
        this.trainerDao = d;
    }

    public String generateUsername(String firstName, String lastName) {
        String base = firstName + "." + lastName;
        Set<String> existing = collectExistingUsernames();

        if (!existing.contains(base)) {
            return base;
        }

        int suffix = 1;
        String candidate = base + suffix;

        while (existing.contains(candidate)) {
            suffix++;
            candidate = base + suffix;
        }

        log.debug("Username '{}' already existed, resolved to '{}'", base, candidate);

        return candidate;
    }

    public String generatePassword() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        return sb.toString();
    }

    private Set<String> collectExistingUsernames() {
        Set<String> u = new HashSet<>();

        traineeDao.findAll().forEach(t -> u.add(t.getUsername()));
        trainerDao.findAll().forEach(t -> u.add(t.getUsername()));

        return u;
    }
}