package com.gym.crm.domain;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {
    private Long userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;
}
