package com.gym.crm;

import com.gym.crm.config.HibernateConfig;
import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.Training;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.domain.User;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.List;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(HibernateConfig.class)) {

            GymFacade facade = context.getBean(GymFacade.class);
            TrainingService trainingService = context.getBean(TrainingService.class);

            List<TrainingType> trainingTypes = trainingService.getTrainingTypes();
            TrainingType cardio = trainingTypes.get(0);

            User trainerUser = new User();
            trainerUser.setFirstName("Anna");
            trainerUser.setLastName("Jones");

            Trainer trainer = new Trainer();
            trainer.setUser(trainerUser);
            trainer.setSpecialization(cardio);

            Trainer savedTrainer = facade.createTrainerProfile(trainer);
            log.info("Created trainer -> username={}, password={}",
                    savedTrainer.getUser().getUsername(), savedTrainer.getUser().getPassword());

            User traineeUser = new User();
            traineeUser.setFirstName("John");
            traineeUser.setLastName("Smith");

            Trainee trainee = new Trainee();
            trainee.setUser(traineeUser);
            trainee.setAddress("Baker Street 221B");
            trainee.setDateOfBirth(LocalDate.of(1990, 5, 20));

            Trainee savedTrainee = facade.createTraineeProfile(trainee);
            log.info("Created trainee -> username={}, password={}",
                    savedTrainee.getUser().getUsername(), savedTrainee.getUser().getPassword());

            Training training = new Training();
            training.setTrainee(savedTrainee);
            training.setTrainer(savedTrainer);
            training.setTrainingName("Morning Cardio");
            training.setTrainingType(cardio);
            training.setTrainingDate(LocalDate.now());
            training.setTrainingDuration(60);

            facade.addTraining(savedTrainee.getUser().getUsername(), savedTrainee.getUser().getPassword(), training);

            List<Training> traineeTrainings = facade.getTraineeTrainings(
                    savedTrainee.getUser().getUsername(), savedTrainee.getUser().getPassword(),
                    null, null, null, null);

            log.info("Trainee trainings count: {}", traineeTrainings.size());
        }
    }
}