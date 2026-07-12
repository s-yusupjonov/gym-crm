package com.gym.crm.dao;

import com.gym.crm.domain.Trainee;
import com.gym.crm.storage.InMemoryStorage;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TraineeDao {

    private static final Logger log = LoggerFactory.getLogger(TraineeDao.class);

    private final InMemoryStorage storage;
    private final AtomicLong idSequence = new AtomicLong(1000);

    @Autowired
    public TraineeDao(InMemoryStorage storage) {
        this.storage = storage;
    }

    public Trainee create(Trainee t) {
        long id = idSequence.incrementAndGet();
        t.setTraineeId(id);
        t.setUserId(id);

        storage.getTraineeStorage().put(id, t);
        log.debug("Created trainee with id={}", id);

        return t;
    }

    public Trainee update(Trainee t) {
        storage.getTraineeStorage().put(t.getTraineeId(), t);
        log.debug("Updated trainee with id={}", t.getTraineeId());

        return t;
    }

    public void delete(Long id) {
        storage.getTraineeStorage().remove(id);
        log.debug("Deleted trainee with id={}", id);
    }

    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(storage.getTraineeStorage().get(id));
    }

    public List<Trainee> findAll() {
        return List.copyOf(storage.getTraineeStorage().values());
    }
}