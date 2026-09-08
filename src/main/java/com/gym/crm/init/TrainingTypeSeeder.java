package com.gym.crm.init;

import com.gym.crm.domain.TrainingType;
import com.gym.crm.repository.TrainingTypeRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainingTypeSeeder {

    private static final Logger log = LoggerFactory.getLogger(TrainingTypeSeeder.class);

    private static final List<String> DEFAULT_TYPES = List.of(
            "CARDIO", "STRENGTH", "YOGA", "CROSSFIT", "PILATES", "ZUMBA");

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeSeeder(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @PostConstruct
    public void seed() {
        if (trainingTypeRepository.count() > 0) {
            return;
        }

        List<TrainingType> types = DEFAULT_TYPES.stream()
                .map(name -> {
                    TrainingType type = new TrainingType();
                    type.setTrainingTypeName(name);
                    return type;
                })
                .toList();

        trainingTypeRepository.saveAll(types);
        log.info("Seeded {} default training types", types.size());
    }
}