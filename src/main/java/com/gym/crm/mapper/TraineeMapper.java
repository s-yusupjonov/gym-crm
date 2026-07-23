package com.gym.crm.mapper;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.User;
import com.gym.crm.dto.trainee.TraineeProfileResponse;
import com.gym.crm.dto.trainee.TraineeRegistrationRequest;
import com.gym.crm.dto.trainee.UpdateTraineeRequest;

public final class TraineeMapper {

    private TraineeMapper() {
    }

    public static Trainee toEntity(TraineeRegistrationRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());
        return trainee;
    }

    public static Trainee toEntity(UpdateTraineeRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setActive(Boolean.TRUE.equals(request.getActive()));

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());
        return trainee;
    }

    /**
     * Maps to the "Get Trainee Profile" response shape - username is left unset (omitted from JSON).
     */
    public static TraineeProfileResponse toProfileResponse(Trainee trainee) {
        TraineeProfileResponse response = new TraineeProfileResponse();
        response.setFirstName(trainee.getUser().getFirstName());
        response.setLastName(trainee.getUser().getLastName());
        response.setDateOfBirth(trainee.getDateOfBirth());
        response.setAddress(trainee.getAddress());
        response.setActive(trainee.getUser().isActive());
        response.setTrainers(ReferenceMapper.toTrainerShortDtoList(trainee.getTrainers().stream().toList()));
        return response;
    }

    /**
     * Maps to the "Update Trainee Profile" response shape - includes the (immutable) username.
     */
    public static TraineeProfileResponse toUpdateResponse(Trainee trainee) {
        TraineeProfileResponse response = toProfileResponse(trainee);
        response.setUsername(trainee.getUser().getUsername());
        return response;
    }
}