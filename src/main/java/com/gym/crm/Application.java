package com.gym.crm;

import com.gym.crm.config.AppConfig;
import com.gym.crm.domain.*;
import com.gym.crm.facade.GymFacade;
import org.slf4j.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {

            GymFacade facade = context.getBean(GymFacade.class);

            Trainee trainee = new Trainee();
            trainee.setFirstName("John");
            trainee.setLastName("Smith");
            trainee.setAddress("Baker Street 221B");

            Trainee saved = facade.createTrainee(trainee);
            log.info("New trainee -> username={}, password={}", saved.getUsername(), saved.getPassword());

            Trainer trainer = new Trainer();
            trainer.setFirstName("Anna");
            trainer.setLastName("Jones");
            trainer.setSpecialization("Cardio");

            Trainer st = facade.createTrainer(trainer);
            log.info("New trainer -> username={}", st.getUsername());

            log.info("Total trainees: {}", facade.getAllTrainees().size());
            log.info("Total trainers: {}", facade.getAllTrainers().size());
            log.info("Total trainings: {}", facade.getAllTrainings().size());
        }
    }
}