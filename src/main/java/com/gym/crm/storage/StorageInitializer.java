package com.gym.crm.storage;
import com.gym.crm.domain.*;
import org.slf4j.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
@Component
public class StorageInitializer implements BeanPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(StorageInitializer.class);
    @Value("${storage.trainees.file}") private String traineesFile;
    @Value("${storage.trainers.file}") private String trainersFile;
    @Value("${storage.trainings.file}") private String trainingsFile;
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof InMemoryStorage storage) {
            log.info("Initializing in-memory storage from data files...");
            loadTrainers(storage, trainersFile);
            loadTrainees(storage, traineesFile);
            loadTrainings(storage, trainingsFile);
            log.info("Storage initialized: {} trainers, {} trainees, {} trainings",
                    storage.getTrainerStorage().size(), storage.getTraineeStorage().size(),
                    storage.getTrainingStorage().size());
        }
        return bean;
    }
    private void loadTrainers(InMemoryStorage s, String file) {
        try (BufferedReader r = openReader(file)) {
            String line; boolean header = true;
            while ((line = r.readLine()) != null) {
                if (header) { header = false; continue; }
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                Trainer t = new Trainer();
                t.setTrainerId(Long.parseLong(p[0].trim())); t.setUserId(t.getTrainerId());
                t.setFirstName(p[1].trim()); t.setLastName(p[2].trim());
                t.setUsername(p[3].trim()); t.setPassword(p[4].trim());
                t.setActive(Boolean.parseBoolean(p[5].trim())); t.setSpecialization(p[6].trim());
                s.getTrainerStorage().put(t.getTrainerId(), t);
            }
        } catch (Exception e) { log.error("Failed to load trainers from {}", file, e); }
    }
    private void loadTrainees(InMemoryStorage s, String file) {
        try (BufferedReader r = openReader(file)) {
            String line; boolean header = true;
            while ((line = r.readLine()) != null) {
                if (header) { header = false; continue; }
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                Trainee t = new Trainee();
                t.setTraineeId(Long.parseLong(p[0].trim())); t.setUserId(t.getTraineeId());
                t.setFirstName(p[1].trim()); t.setLastName(p[2].trim());
                t.setUsername(p[3].trim()); t.setPassword(p[4].trim());
                t.setActive(Boolean.parseBoolean(p[5].trim()));
                t.setDateOfBirth(LocalDate.parse(p[6].trim())); t.setAddress(p[7].trim());
                s.getTraineeStorage().put(t.getTraineeId(), t);
            }
        } catch (Exception e) { log.error("Failed to load trainees from {}", file, e); }
    }
    private void loadTrainings(InMemoryStorage s, String file) {
        try (BufferedReader r = openReader(file)) {
            String line; boolean header = true;
            while ((line = r.readLine()) != null) {
                if (header) { header = false; continue; }
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                Training t = new Training();
                t.setTrainingId(Long.parseLong(p[0].trim()));
                t.setTraineeId(Long.parseLong(p[1].trim()));
                t.setTrainerId(Long.parseLong(p[2].trim()));
                t.setTrainingName(p[3].trim());
                t.setTrainingType(TrainingType.valueOf(p[4].trim().toUpperCase()));
                t.setTrainingDate(LocalDate.parse(p[5].trim()));
                t.setTrainingDuration(Integer.parseInt(p[6].trim()));
                s.getTrainingStorage().put(t.getTrainingId(), t);
            }
        } catch (Exception e) { log.error("Failed to load trainings from {}", file, e); }
    }
    private BufferedReader openReader(String file) throws Exception {
        InputStream is = new ClassPathResource(file).getInputStream();
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    }
}
