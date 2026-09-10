package com.gym.crm.controller;

import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.dto.common.ActiveStatusRequest;
import com.gym.crm.dto.common.RegistrationResponse;
import com.gym.crm.dto.trainer.TrainerProfileResponse;
import com.gym.crm.dto.trainer.TrainerRegistrationRequest;
import com.gym.crm.dto.trainer.UpdateTrainerRequest;
import com.gym.crm.mapper.TrainerMapper;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainers")
@Validated
@Api(tags = "Trainer")
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping
    @ApiOperation(value = "Trainer registration", notes = "Creates a trainer profile and generates username/password. "
            + "Does not require authentication.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Trainer created"),
            @ApiResponse(code = 400, message = "Request is invalid, or the specialization id does not exist")
    })
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody TrainerRegistrationRequest request) {
        TrainingType specialization = trainerService.getSpecializationById(request.getSpecializationId());
        Trainer trainer = TrainerMapper.toEntity(request, specialization);
        Trainer saved = trainerService.createTrainerProfile(trainer);
        RegistrationResponse response =
                new RegistrationResponse(saved.getUser().getUsername(), saved.getUser().getRawPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{username}")
    @ApiOperation(value = "Get trainer profile", notes = "Safe, idempotent read of a trainer's profile.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer profile returned"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    public ResponseEntity<TrainerProfileResponse> getProfile(
            @ApiParam(value = "Trainer username", required = true)
            @PathVariable @NotBlank(message = "Username is required") String username) {
        Trainer trainer = trainerService.getByUsername(username);
        return ResponseEntity.ok(TrainerMapper.toProfileResponse(trainer));
    }

    @PutMapping("/{username}")
    @ApiOperation(value = "Update trainer profile", notes = "Full replace of the mutable trainer profile fields. "
            + "Username and specialization are read-only and cannot be changed through this endpoint.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer profile updated"),
            @ApiResponse(code = 400, message = "Request is invalid"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    public ResponseEntity<TrainerProfileResponse> updateProfile(
            @ApiParam(value = "Trainer username", required = true)
            @PathVariable @NotBlank(message = "Username is required") String username,
            @Valid @RequestBody UpdateTrainerRequest request) {
        Trainer current = trainerService.getByUsername(username);
        Trainer updates = TrainerMapper.toEntity(request, current.getSpecialization());
        Trainer updated = trainerService.updateTrainerProfile(username, updates);
        return ResponseEntity.ok(TrainerMapper.toUpdateResponse(updated));
    }

    @PatchMapping("/{username}/status")
    @ApiOperation(value = "Activate/De-activate trainer", notes = "Partial update of only the active flag. "
            + "Not idempotent: it is a state-transition action rather than a full-resource replace.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Active status updated"),
            @ApiResponse(code = 400, message = "Request is invalid"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    public ResponseEntity<Void> setActive(
            @ApiParam(value = "Trainer username", required = true)
            @PathVariable @NotBlank(message = "Username is required") String username,
            @Valid @RequestBody ActiveStatusRequest request) {
        trainerService.setActive(username, request.getActive());
        return ResponseEntity.ok().build();
    }
}