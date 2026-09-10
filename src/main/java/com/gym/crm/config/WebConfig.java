package com.gym.crm.config;

import com.gym.crm.logging.RestLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public RestLoggingInterceptor restLoggingInterceptor() {
        return new RestLoggingInterceptor();
    }

    @Bean
    public WebMvcConfigurer mvcInterceptorConfigurer(RestLoggingInterceptor restLoggingInterceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(restLoggingInterceptor).addPathPatterns("/**");
            }
        };
    }
}