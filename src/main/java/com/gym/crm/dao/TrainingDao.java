package com.gym.crm.dao;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TrainingDao extends AbstractDao<Training> {

    public TrainingDao(SessionFactory sessionFactory) {
        super(sessionFactory, Training.class);
    }

    public List<Training> findTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate,
                                               String trainerName, String trainingTypeName) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> root = query.from(Training.class);

        Fetch<Training, Trainee> traineeFetch = root.fetch("trainee");
        Join<?, ?> traineeUserJoin = (Join<?, ?>) traineeFetch.fetch("user");

        Fetch<Training, Trainer> trainerFetch = root.fetch("trainer");
        Join<?, ?> trainerUserJoin = (Join<?, ?>) trainerFetch.fetch("user");

        Fetch<Training, ?> trainingTypeFetch = root.fetch("trainingType");
        Join<?, ?> trainingTypeJoin = (Join<?, ?>) trainingTypeFetch;

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(traineeUserJoin.get("username"), traineeUsername));

        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("trainingDate"), toDate));
        }
        if (trainerName != null && !trainerName.isBlank()) {
            String pattern = "%" + trainerName + "%";
            predicates.add(cb.or(
                    cb.like(trainerUserJoin.get("firstName"), pattern),
                    cb.like(trainerUserJoin.get("lastName"), pattern)));
        }
        if (trainingTypeName != null && !trainingTypeName.isBlank()) {
            predicates.add(cb.equal(trainingTypeJoin.get("trainingTypeName"), trainingTypeName));
        }

        query.select(root).distinct(true).where(predicates.toArray(new Predicate[0]));

        return currentSession().createQuery(query).list();
    }

    public List<Training> findTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate,
                                               String traineeName) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> root = query.from(Training.class);

        Fetch<Training, Trainee> traineeFetch = root.fetch("trainee");
        Join<?, ?> traineeUserJoin = (Join<?, ?>) traineeFetch.fetch("user");

        Fetch<Training, Trainer> trainerFetch = root.fetch("trainer");
        Join<?, ?> trainerUserJoin = (Join<?, ?>) trainerFetch.fetch("user");

        root.fetch("trainingType");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(trainerUserJoin.get("username"), trainerUsername));

        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("trainingDate"), toDate));
        }
        if (traineeName != null && !traineeName.isBlank()) {
            String pattern = "%" + traineeName + "%";
            predicates.add(cb.or(
                    cb.like(traineeUserJoin.get("firstName"), pattern),
                    cb.like(traineeUserJoin.get("lastName"), pattern)));
        }

        query.select(root).distinct(true).where(predicates.toArray(new Predicate[0]));

        return currentSession().createQuery(query).list();
    }
}