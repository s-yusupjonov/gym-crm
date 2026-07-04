package com.gym.crm.domain;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Trainer extends User {
    private Long trainerId;
    private String specialization;
}
