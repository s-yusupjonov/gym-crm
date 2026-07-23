package com.gym.crm.controller;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.Training;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.dto.common.TrainingTypeDto;
import com.gym.crm.dto.training.AddTrainingRequest;
import com.gym.crm.dto.training.TraineeTrainingResponse;
import com.gym.crm.dto.training.TrainerTrainingResponse;
import com.gym.crm.mapper.ReferenceMapper;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trainings")
@Validated
@Api(tags = "Training")
public class TrainingController {

    private final TrainingService trainingService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public TrainingController(TrainingService trainingService, TraineeService traineeService,
                              TrainerService trainerService) {
        this.trainingService = trainingService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    @PostMapping
    @ApiOperation(value = "Add training", notes = "Creates a new training session for a trainee/trainer pair. "
            + "The training type is derived from the assigned trainer's specialization.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Training added"),
            @ApiResponse(code = 400, message = "Request is invalid"),
            @ApiResponse(code = 404, message = "Trainee or trainer not found")
    })
    public ResponseEntity<Void> addTraining(@Valid @RequestBody AddTrainingRequest request) {
        Trainee trainee = traineeService.getByUsername(request.getTraineeUsername());
        Trainer trainer = trainerService.getByUsername(request.getTrainerUsername());

        Training training = TrainingMapper.toEntity(request, trainee, trainer);
        trainingService.addTraining(training);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/trainee/{username}")
    @ApiOperation(value = "Get trainee trainings list", notes = "Safe, idempotent read of a trainee's trainings, "
            + "optionally filtered by period, trainer name, and training type.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainings returned"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<List<TraineeTrainingResponse>> getTraineeTrainings(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable @NotBlank(message = "Username is required") String username,
            @ApiParam(value = "Period from (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @ApiParam(value = "Period to (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @ApiParam(value = "Trainer name filter (matches first or last name)")
            @RequestParam(required = false) String trainerName,
            @ApiParam(value = "Training type name filter")
            @RequestParam(required = false) String trainingType) {
        traineeService.getByUsername(username);

        List<Training> trainings = trainingService.getTraineeTrainings(username, periodFrom, periodTo,
                trainerName, trainingType);

        return ResponseEntity.ok(TrainingMapper.toTraineeTrainingResponseList(trainings));
    }

    @GetMapping("/trainer/{username}")
    @ApiOperation(value = "Get trainer trainings list", notes = "Safe, idempotent read of a trainer's trainings, "
            + "optionally filtered by period and trainee name.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainings returned"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainerTrainings(
            @ApiParam(value = "Trainer username", required = true)
            @PathVariable @NotBlank(message = "Username is required") String username,
            @ApiParam(value = "Period from (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @ApiParam(value = "Period to (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @ApiParam(value = "Trainee name filter (matches first or last name)")
            @RequestParam(required = false) String traineeName) {
        trainerService.getByUsername(username);

        List<Training> trainings = trainingService.getTrainerTrainings(username, periodFrom, periodTo, traineeName);

        return ResponseEntity.ok(TrainingMapper.toTrainerTrainingResponseList(trainings));
    }

    @GetMapping("/types")
    @ApiOperation(value = "Get training types", notes = "Returns the constant, read-only list of training types. "
            + "This reference list cannot be modified through the application.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Training types returned")
    })
    public ResponseEntity<List<TrainingTypeDto>> getTrainingTypes() {
        List<TrainingType> trainingTypes = trainingService.getTrainingTypes();
        List<TrainingTypeDto> dtos = trainingTypes.stream()
                .map(ReferenceMapper::toTrainingTypeDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }
}