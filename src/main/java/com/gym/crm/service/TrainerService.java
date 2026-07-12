package com.gym.crm.service;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.domain.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
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
    public void setUserProfileService(UserProfileService s) {
        this.userProfileService = s;
    }

    public Trainer create(Trainer t) {
        t.setUsername(userProfileService.generateUsername(t.getFirstName(), t.getLastName()));
        t.setPassword(userProfileService.generatePassword());
        t.setActive(true);

        Trainer saved = trainerDao.create(t);
        log.info("Created trainer profile: username={}, id={}", saved.getUsername(), saved.getTrainerId());

        return saved;
    }

    public Trainer update(Trainer t) {
        log.info("Updating trainer id={}", t.getTrainerId());
        return trainerDao.update(t);
    }

    public Optional<Trainer> select(Long id) {
        return trainerDao.findById(id);
    }

    public List<Trainer> selectAll() {
        return trainerDao.findAll();
    }
}