package com.gym.crm.config;

import com.gym.crm.logging.RestLoggingInterceptor;
import com.gym.crm.security.AuthenticationInterceptor;
import com.gym.crm.service.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Holds the interceptor bean definitions separately from WebConfig.
 *
 * These used to be @Bean methods directly on WebConfig, but WebConfig's constructor also
 * requires these same two beans - which meant Spring had to fully construct WebConfig in order
 * to call a @Bean factory method ON WebConfig, before WebConfig itself could be constructed.
 * That circular constructor dependency only breaks when the servlet DispatcherServlet actually
 * refreshes the full web application context (which standalone MockMvc tests never do), so it
 * only surfaced once the app was deployed end-to-end.
 */
@Configuration
public class InterceptorConfig {

    @Bean
    public RestLoggingInterceptor restLoggingInterceptor() {
        return new RestLoggingInterceptor();
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor(AuthenticationService authenticationService) {
        return new AuthenticationInterceptor(authenticationService);
    }
}