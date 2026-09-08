package com.gym.crm.config;

import com.gym.crm.logging.RestLoggingInterceptor;
import com.gym.crm.security.AuthenticationInterceptor;
import com.gym.crm.service.AuthenticationService;
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
    public AuthenticationInterceptor authenticationInterceptor(AuthenticationService authenticationService) {
        return new AuthenticationInterceptor(authenticationService);
    }

    @Bean
    public WebMvcConfigurer mvcInterceptorConfigurer(RestLoggingInterceptor restLoggingInterceptor,
                                                     AuthenticationInterceptor authenticationInterceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(restLoggingInterceptor).addPathPatterns("/**");
                registry.addInterceptor(authenticationInterceptor)
                        .addPathPatterns("/api/**")
                        .excludePathPatterns("/api/login");
            }
        };
    }
}