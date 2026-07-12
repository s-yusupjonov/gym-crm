package com.gym.crm.dao;

import com.gym.crm.domain.Trainer;
import com.gym.crm.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TrainerDao {

    private static final Logger log = LoggerFactory.getLogger(TrainerDao.class);

    private final InMemoryStorage storage;
    private final AtomicLong idSequence = new AtomicLong(2000);

    @Autowired
    public TrainerDao(InMemoryStorage storage) {
        this.storage = storage;
    }

    public Trainer create(Trainer t) {
        long id = idSequence.incrementAndGet();
        t.setTrainerId(id);
        t.setUserId(id);

        storage.getTrainerStorage().put(id, t);
        log.debug("Created trainer with id={}", id);

        return t;
    }

    public Trainer update(Trainer t) {
        storage.getTrainerStorage().put(t.getTrainerId(), t);
        log.debug("Updated trainer with id={}", t.getTrainerId());

        return t;
    }

    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(storage.getTrainerStorage().get(id));
    }

    public List<Trainer> findAll() {
        return List.copyOf(storage.getTrainerStorage().values());
    }
}