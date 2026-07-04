package com.gym.crm.dao;
import com.gym.crm.domain.Training;
import com.gym.crm.storage.InMemoryStorage;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
@Repository
public class TrainingDao {
    private static final Logger log = LoggerFactory.getLogger(TrainingDao.class);
    private final InMemoryStorage storage;
    private final AtomicLong idSequence = new AtomicLong(3000);
    @Autowired public TrainingDao(InMemoryStorage storage) { this.storage = storage; }
    public Training create(Training t) {
        long id = idSequence.incrementAndGet();
        t.setTrainingId(id);
        storage.getTrainingStorage().put(id, t);
        log.debug("Created training with id={}", id);
        return t;
    }
    public Optional<Training> findById(Long id) { return Optional.ofNullable(storage.getTrainingStorage().get(id)); }
    public List<Training> findAll() { return List.copyOf(storage.getTrainingStorage().values()); }
}
