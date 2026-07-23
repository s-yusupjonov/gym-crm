package com.gym.crm.init;

import com.gym.crm.domain.TrainingType;
import jakarta.annotation.PostConstruct;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainingTypeSeeder {

    private static final Logger log = LoggerFactory.getLogger(TrainingTypeSeeder.class);

    private static final List<String> DEFAULT_TYPES = List.of(
            "CARDIO", "STRENGTH", "YOGA", "CROSSFIT", "PILATES", "ZUMBA");

    private final SessionFactory sessionFactory;

    @Autowired
    public TrainingTypeSeeder(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @PostConstruct
    public void seed() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Long count = session.createQuery("select count(t) from TrainingType t", Long.class).uniqueResult();

            if (count != null && count == 0) {
                for (String name : DEFAULT_TYPES) {
                    TrainingType type = new TrainingType();
                    type.setTrainingTypeName(name);
                    session.persist(type);
                }
                log.info("Seeded {} default training types", DEFAULT_TYPES.size());
            }

            tx.commit();
        }
    }
}