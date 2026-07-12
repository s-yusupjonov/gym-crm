package com.gym.crm.service;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.domain.Training;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    private final TrainingDao trainingDao;

    @Autowired
    public TrainingService(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    public Training create(Training t) {
        Training saved = trainingDao.create(t);
        log.info("Created training profile: id={}, name={}", saved.getTrainingId(), saved.getTrainingName());

        return saved;
    }

    public Optional<Training> select(Long id) {
        return trainingDao.findById(id);
    }

    public List<Training> selectAll() {
        return trainingDao.findAll();
    }
}