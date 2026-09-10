package com.gym.crm.repository;

import com.gym.crm.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("select distinct t from Training t "
            + "join fetch t.trainee te join fetch te.user "
            + "join fetch t.trainer tr join fetch tr.user "
            + "join fetch t.trainingType "
            + "where te.user.username = :traineeUsername "
            + "and (cast(:fromDate as date) is null or t.trainingDate >= cast(:fromDate as date)) "
            + "and (cast(:toDate as date) is null or t.trainingDate <= cast(:toDate as date)) "
            + "and (cast(:trainerName as string) is null "
            + "or tr.user.firstName like concat('%', cast(:trainerName as string), '%') "
            + "or tr.user.lastName like concat('%', cast(:trainerName as string), '%')) "
            + "and (cast(:trainingTypeName as string) is null "
            + "or t.trainingType.trainingTypeName = cast(:trainingTypeName as string))")
    List<Training> findTraineeTrainings(@Param("traineeUsername") String traineeUsername,
                                        @Param("fromDate") LocalDate fromDate,
                                        @Param("toDate") LocalDate toDate,
                                        @Param("trainerName") String trainerName,
                                        @Param("trainingTypeName") String trainingTypeName);

    @Query("select distinct t from Training t "
            + "join fetch t.trainee te join fetch te.user "
            + "join fetch t.trainer tr join fetch tr.user "
            + "join fetch t.trainingType "
            + "where tr.user.username = :trainerUsername "
            + "and (cast(:fromDate as date) is null or t.trainingDate >= cast(:fromDate as date)) "
            + "and (cast(:toDate as date) is null or t.trainingDate <= cast(:toDate as date)) "
            + "and (cast(:traineeName as string) is null "
            + "or te.user.firstName like concat('%', cast(:traineeName as string), '%') "
            + "or te.user.lastName like concat('%', cast(:traineeName as string), '%'))")
    List<Training> findTrainerTrainings(@Param("trainerUsername") String trainerUsername,
                                        @Param("fromDate") LocalDate fromDate,
                                        @Param("toDate") LocalDate toDate,
                                        @Param("traineeName") String traineeName);
}