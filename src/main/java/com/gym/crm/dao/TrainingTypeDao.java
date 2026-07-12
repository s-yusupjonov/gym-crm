package com.gym.crm.dao;

import com.gym.crm.domain.TrainingType;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingTypeDao extends AbstractDao<TrainingType> {

    @Autowired
    public TrainingTypeDao(SessionFactory sessionFactory) {
        super(sessionFactory, TrainingType.class);
    }
}