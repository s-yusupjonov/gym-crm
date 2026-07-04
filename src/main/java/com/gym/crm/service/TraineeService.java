package com.gym.crm.service;
import com.gym.crm.dao.TraineeDao;
import com.gym.crm.domain.Trainee;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class TraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);
    private final TraineeDao traineeDao;
    private UserProfileService userProfileService;
    @Autowired public TraineeService(TraineeDao traineeDao) { this.traineeDao = traineeDao; }
    @Autowired public void setUserProfileService(UserProfileService s) { this.userProfileService = s; }
    public Trainee create(Trainee t) {
        t.setUsername(userProfileService.generateUsername(t.getFirstName(), t.getLastName()));
        t.setPassword(userProfileService.generatePassword());
        t.setActive(true);
        Trainee saved = traineeDao.create(t);
        log.info("Created trainee profile: username={}, id={}", saved.getUsername(), saved.getTraineeId());
        return saved;
    }
    public Trainee update(Trainee t) { log.info("Updating trainee id={}", t.getTraineeId()); return traineeDao.update(t); }
    public void delete(Long id) { log.info("Deleting trainee id={}", id); traineeDao.delete(id); }
    public Optional<Trainee> select(Long id) { return traineeDao.findById(id); }
    public List<Trainee> selectAll() { return traineeDao.findAll(); }
}
