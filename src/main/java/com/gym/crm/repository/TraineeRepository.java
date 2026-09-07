package com.gym.crm.dao;

import com.gym.crm.domain.Trainee;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TraineeDao extends AbstractDao<Trainee> {

    public TraineeDao(SessionFactory sessionFactory) {
        super(sessionFactory, Trainee.class);
    }

    public Optional<Trainee> findByUsername(String username) {
        return currentSession()
                .createQuery("select distinct t from Trainee t "
                                + "join fetch t.user u "
                                + "left join fetch t.trainers tr "
                                + "left join fetch tr.user "
                                + "left join fetch tr.specialization "
                                + "where u.username = :username",
                        Trainee.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }
}