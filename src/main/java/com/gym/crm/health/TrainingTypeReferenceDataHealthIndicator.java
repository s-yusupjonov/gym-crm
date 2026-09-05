package com.gym.crm.health;

import com.gym.crm.dao.TrainingTypeDao;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TrainingTypeReferenceDataHealthIndicator implements HealthIndicator {

    private final TrainingTypeDao trainingTypeDao;

    public TrainingTypeReferenceDataHealthIndicator(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Health health() {
        int typeCount = trainingTypeDao.findAll().size();
        if (typeCount > 0) {
            return Health.up().withDetail("trainingTypes", typeCount).build();
        }
        return Health.down()
                .withDetail("trainingTypes", 0)
                .withDetail("reason", "no training types seeded")
                .build();
    }
}