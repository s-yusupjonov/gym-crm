package com.gym.crm.health;

import com.gym.crm.repository.TrainingTypeRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TrainingTypeReferenceDataHealthIndicator implements HealthIndicator {

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeReferenceDataHealthIndicator(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Health health() {
        long typeCount = trainingTypeRepository.count();
        if (typeCount > 0) {
            return Health.up().withDetail("trainingTypes", typeCount).build();
        }
        return Health.down()
                .withDetail("trainingTypes", 0)
                .withDetail("reason", "no training types seeded")
                .build();
    }
}