package com.gym.crm.dao;

import com.gym.crm.domain.TrainingType;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingTypeDao extends AbstractDao<TrainingType> {

    public TrainingTypeDao(SessionFactory sessionFactory) {
        super(sessionFactory, TrainingType.class);
    }
}