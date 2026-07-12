package com.gym.crm.domain;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Trainee extends User {

    private Long traineeId;
    private LocalDate dateOfBirth;
    private String address;
}