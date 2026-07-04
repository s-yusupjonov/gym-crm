package com.gym.crm.config;
import com.gym.crm.domain.*;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import java.util.*;
@Configuration
@ComponentScan(basePackages = "com.gym.crm")
@PropertySource("classpath:application.properties")
public class AppConfig {
    @Bean public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    @Bean public Map<Long, Trainee> traineeStorage() { return new HashMap<>(); }
    @Bean public Map<Long, Trainer> trainerStorage() { return new HashMap<>(); }
    @Bean public Map<Long, Training> trainingStorage() { return new HashMap<>(); }
}
