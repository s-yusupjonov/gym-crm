package com.gym.crm.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(HibernateConfig.class)
@ComponentScan(basePackages = {
        "com.gym.crm.dao",
        "com.gym.crm.service",
        "com.gym.crm.mapper",
        "com.gym.crm.util",
        "com.gym.crm.init"
})
public class RootConfig {
}