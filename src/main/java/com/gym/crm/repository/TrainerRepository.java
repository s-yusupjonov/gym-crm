package com.gym.crm.repository;

import com.gym.crm.domain.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("select distinct t from Trainer t "
            + "join fetch t.user u "
            + "left join fetch t.specialization "
            + "left join fetch t.trainees te "
            + "left join fetch te.user "
            + "where u.username = :username")
    Optional<Trainer> findByUsername(@Param("username") String username);

    @Query("select t from Trainer t "
            + "join fetch t.user u "
            + "left join fetch t.specialization "
            + "where u.username in :usernames")
    List<Trainer> findByUsernames(@Param("usernames") List<String> usernames);

    @Query("select t from Trainer t "
            + "join fetch t.user u "
            + "left join fetch t.specialization "
            + "where t not in "
            + "(select tr from Trainee tn join tn.trainers tr join tn.user tu where tu.username = :username)")
    List<Trainer> findNotAssignedToTrainee(@Param("username") String traineeUsername);
}