package com.gym.crm.dto.trainee;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UpdateTraineeRequest {

    @NotBlank(message = "First name is required")
    @ApiModelProperty(value = "First name", required = true, example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @ApiModelProperty(value = "Last name", required = true, example = "Doe")
    private String lastName;

    @ApiModelProperty(value = "Date of birth (optional)", example = "1995-05-20")
    private LocalDate dateOfBirth;

    @ApiModelProperty(value = "Address (optional)", example = "123 Main St")
    private String address;

    @NotNull(message = "isActive is required")
    @ApiModelProperty(value = "Active status", required = true, example = "true")
    private Boolean active;
}