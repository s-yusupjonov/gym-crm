package com.gym.crm.dao;

import com.gym.crm.domain.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
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

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("trainee").get("user").get("username"), traineeUsername));

        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("trainingDate"), toDate));
        }
        if (trainerName != null && !trainerName.isBlank()) {
            String pattern = "%" + trainerName + "%";
            predicates.add(cb.or(
                    cb.like(root.get("trainer").get("user").get("firstName"), pattern),
                    cb.like(root.get("trainer").get("user").get("lastName"), pattern)));
        }
        if (trainingTypeName != null && !trainingTypeName.isBlank()) {
            predicates.add(cb.equal(root.get("trainingType").get("trainingTypeName"), trainingTypeName));
        }

        query.select(root).where(predicates.toArray(new Predicate[0]));

        return currentSession().createQuery(query).list();
    }

    public List<Training> findTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate,
                                               String traineeName) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> root = query.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("trainer").get("user").get("username"), trainerUsername));

        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("trainingDate"), toDate));
        }
        if (traineeName != null && !traineeName.isBlank()) {
            String pattern = "%" + traineeName + "%";
            predicates.add(cb.or(
                    cb.like(root.get("trainee").get("user").get("firstName"), pattern),
                    cb.like(root.get("trainee").get("user").get("lastName"), pattern)));
        }

        query.select(root).where(predicates.toArray(new Predicate[0]));

        return currentSession().createQuery(query).list();
    }
}