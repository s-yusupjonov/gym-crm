package com.gym.crm.dao;

import com.gym.crm.domain.Trainer;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDao extends AbstractDao<Trainer> {

    public TrainerDao(SessionFactory sessionFactory) {
        super(sessionFactory, Trainer.class);
    }

    public Optional<Trainer> findByUsername(String username) {
        return currentSession()
                .createQuery("select distinct t from Trainer t "
                                + "join fetch t.user u "
                                + "left join fetch t.specialization "
                                + "left join fetch t.trainees te "
                                + "left join fetch te.user "
                                + "where u.username = :username",
                        Trainer.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    public List<Trainer> findByUsernames(List<String> usernames) {
        return currentSession()
                .createQuery("select t from Trainer t "
                                + "join fetch t.user u "
                                + "left join fetch t.specialization "
                                + "where u.username in :usernames",
                        Trainer.class)
                .setParameter("usernames", usernames)
                .list();
    }

    public List<Trainer> findNotAssignedToTrainee(String traineeUsername) {
        return currentSession()
                .createQuery(
                        "select t from Trainer t "
                                + "join fetch t.user u "
                                + "left join fetch t.specialization "
                                + "where t not in "
                                + "(select tr from Trainee tn join tn.trainers tr join tn.user tu where tu.username = :username)",
                        Trainer.class)
                .setParameter("username", traineeUsername)
                .list();
    }
}