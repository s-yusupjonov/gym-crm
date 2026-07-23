package com.gym.crm.mapper;

import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.domain.User;
import com.gym.crm.dto.trainer.TrainerProfileResponse;
import com.gym.crm.dto.trainer.TrainerRegistrationRequest;
import com.gym.crm.dto.trainer.UpdateTrainerRequest;

public final class TrainerMapper {

    private TrainerMapper() {
    }

    public static Trainer toEntity(TrainerRegistrationRequest request, TrainingType specialization) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        return trainer;
    }

    /**
     * Specialization is read-only on update, so the current specialization is always carried over unchanged.
     */
    public static Trainer toEntity(UpdateTrainerRequest request, TrainingType currentSpecialization) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setActive(Boolean.TRUE.equals(request.getActive()));

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(currentSpecialization);
        return trainer;
    }

    /**
     * Maps to the "Get Trainer Profile" response shape - username is left unset (omitted from JSON).
     */
    public static TrainerProfileResponse toProfileResponse(Trainer trainer) {
        TrainerProfileResponse response = new TrainerProfileResponse();
        response.setFirstName(trainer.getUser().getFirstName());
        response.setLastName(trainer.getUser().getLastName());
        response.setSpecialization(ReferenceMapper.toTrainingTypeDto(trainer.getSpecialization()));
        response.setActive(trainer.getUser().isActive());
        response.setTrainees(ReferenceMapper.toTraineeShortDtoList(trainer.getTrainees().stream().toList()));
        return response;
    }

    /**
     * Maps to the "Update Trainer Profile" response shape - includes the (immutable) username.
     */
    public static TrainerProfileResponse toUpdateResponse(Trainer trainer) {
        TrainerProfileResponse response = toProfileResponse(trainer);
        response.setUsername(trainer.getUser().getUsername());
        return response;
    }
}