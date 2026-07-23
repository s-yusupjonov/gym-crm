package com.gym.crm.controller;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.dto.common.ActiveStatusRequest;
import com.gym.crm.dto.common.RegistrationResponse;
import com.gym.crm.dto.common.TrainerShortDto;
import com.gym.crm.dto.trainee.TraineeProfileResponse;
import com.gym.crm.dto.trainee.TraineeRegistrationRequest;
import com.gym.crm.dto.trainee.UpdateTraineeRequest;
import com.gym.crm.dto.trainee.UpdateTraineeTrainersRequest;
import com.gym.crm.mapper.ReferenceMapper;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trainees")
@Validated
@Api(tags = "Trainee")
public class TraineeController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public TraineeController(TraineeService traineeService, TrainerService trainerService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    @PostMapping
    @ApiOperation(value = "Trainee registration", notes = "Creates a trainee profile and generates username/password. "
            + "Does not require authentication.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Trainee created"),
            @ApiResponse(code = 400, message = "Request is invalid")
    })
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody TraineeRegistrationRequest request) {
        Trainee trainee = TraineeMapper.toEntity(request);
        Trainee saved = traineeService.createTraineeProfile(trainee);
        RegistrationResponse response =
                new RegistrationResponse(saved.getUser().getUsername(), saved.getUser().getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{username}")
    @ApiOperation(value = "Get trainee profile", notes = "Safe, idempotent read of a trainee's profile.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee profile returned"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<TraineeProfileResponse> getProfile(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable @NotBlank(message = "Username is required") String username) {
        Trainee trainee = traineeService.getByUsername(username);
        return ResponseEntity.ok(TraineeMapper.toProfileResponse(trainee));
    }

    @PutMapping("/{username}")
    @ApiOperation(value = "Update trainee profile", notes = "Full replace of the mutable trainee profile fields. "
            + "Username is immutable and cannot be changed.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee profile updated"),
            @ApiResponse(code = 400, message = "Request is invalid"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<TraineeProfileResponse> updateProfile(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable @NotBlank(message = "Username is required") String username,
            @Valid @RequestBody UpdateTraineeRequest request) {
        Trainee updates = TraineeMapper.toEntity(request);
        Trainee updated = traineeService.updateTraineeProfile(username, updates);
        return ResponseEntity.ok(TraineeMapper.toUpdateResponse(updated));
    }

    @DeleteMapping("/{username}")
    @ApiOperation(value = "Delete trainee profile", notes = "Hard delete. Cascades to the trainee's trainings.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee deleted"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<Void> delete(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable @NotBlank(message = "Username is required") String username) {
        traineeService.deleteByUsername(username);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{username}/status")
    @ApiOperation(value = "Activate/De-activate trainee", notes = "Partial update of only the active flag. "
            + "Not idempotent: it is a state-transition action rather than a full-resource replace.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Active status updated"),
            @ApiResponse(code = 400, message = "Request is invalid"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<Void> setActive(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable @NotBlank(message = "Username is required") String username,
            @Valid @RequestBody ActiveStatusRequest request) {
        traineeService.setActive(username, request.getActive());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/unassigned-trainers")
    @ApiOperation(value = "Get active trainers not assigned to a trainee",
            notes = "Returns active trainers who are not yet assigned to the given trainee.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainers returned"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<List<TrainerShortDto>> getUnassignedActiveTrainers(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable @NotBlank(message = "Username is required") String username) {
        traineeService.getByUsername(username);

        List<Trainer> activeUnassigned = trainerService.getTrainersNotAssignedToTrainee(username).stream()
                .filter(trainer -> trainer.getUser().isActive())
                .toList();

        return ResponseEntity.ok(ReferenceMapper.toTrainerShortDtoList(activeUnassigned));
    }

    @PutMapping("/{username}/trainers")
    @ApiOperation(value = "Update trainee's trainer list",
            notes = "Replaces the full set of trainers assigned to the trainee.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer list updated"),
            @ApiResponse(code = 400, message = "One or more trainer usernames do not exist"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<List<TrainerShortDto>> updateTrainersList(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable @NotBlank(message = "Username is required") String username,
            @Valid @RequestBody UpdateTraineeTrainersRequest request) {
        List<String> trainerUsernames = request.getTrainers().stream()
                .map(UpdateTraineeTrainersRequest.TrainerUsername::getTrainerUsername)
                .toList();

        List<Trainer> trainers = traineeService.updateTrainersList(username, trainerUsernames);
        return ResponseEntity.ok(ReferenceMapper.toTrainerShortDtoList(trainers));
    }
}