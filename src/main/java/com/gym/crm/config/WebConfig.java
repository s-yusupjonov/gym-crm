package com.gym.crm.config;

import com.gym.crm.logging.RestLoggingInterceptor;
import com.gym.crm.security.AuthenticationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Boot auto-configures the DispatcherServlet, Jackson ObjectMapper (JavaTimeModule included,
 * timestamps disabled by default) and Bean Validation, so this class is only responsible for the
 * interceptor chain that isn't covered by autoconfiguration.
 */
@Configuration
@Import(InterceptorConfig.class)
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
}