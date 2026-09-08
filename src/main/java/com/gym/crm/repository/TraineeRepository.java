package com.gym.crm.repository;

import com.gym.crm.domain.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    @Query("select distinct t from Trainee t "
            + "join fetch t.user u "
            + "left join fetch t.trainers tr "
            + "left join fetch tr.user "
            + "left join fetch tr.specialization "
            + "where u.username = :username")
    Optional<Trainee> findByUsername(@Param("username") String username);
}