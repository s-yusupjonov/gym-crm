package com.gym.crm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.logging.RestLoggingInterceptor;
import com.gym.crm.security.AuthenticationInterceptor;
import com.gym.crm.service.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.gym.crm.controller")
public class WebConfig implements WebMvcConfigurer {

    private final RestLoggingInterceptor restLoggingInterceptor;
    private final AuthenticationInterceptor authenticationInterceptor;

    public WebConfig(RestLoggingInterceptor restLoggingInterceptor, AuthenticationInterceptor authenticationInterceptor) {
        this.restLoggingInterceptor = restLoggingInterceptor;
        this.authenticationInterceptor = authenticationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Logging runs first so failed-auth attempts are still logged in RestLoggingInterceptor#afterCompletion.
        registry.addInterceptor(restLoggingInterceptor).addPathPatterns("/**");

        // Enforces credential checks on every endpoint except registration; /api/login authenticates itself.
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/login");
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor(AuthenticationService authenticationService) {
        return new AuthenticationInterceptor(authenticationService);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
        converters.add(0, new MappingJackson2HttpMessageConverter(objectMapper()));
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean
    public LocalValidatorFactoryBean mvcValidator() {
        return new LocalValidatorFactoryBean();
    }

    @Override
    public org.springframework.validation.Validator getValidator() {
        return mvcValidator();
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(mvcValidator());
        return processor;
    }

    @Bean
    public RestLoggingInterceptor restLoggingInterceptor() {
        return new RestLoggingInterceptor();
    }
}