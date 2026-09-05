package com.gym.crm.security;

import com.gym.crm.exception.AuthenticationException;
import com.gym.crm.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BASIC_PREFIX = "Basic ";
    private static final String TRAINEES_PATH = "/api/trainees";
    private static final String TRAINERS_PATH = "/api/trainers";

    private final AuthenticationService authenticationService;

    public AuthenticationInterceptor(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (HttpMethod.OPTIONS.matches(request.getMethod()) || isRegistrationRequest(request)) {
            return true;
        }

        String[] credentials = extractCredentials(request);
        authenticationService.authenticate(credentials[0], credentials[1]);
        return true;
    }

    private boolean isRegistrationRequest(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            return false;
        }
        String path = request.getServletPath();
        return TRAINEES_PATH.equals(path) || TRAINERS_PATH.equals(path);
    }

    private String[] extractCredentials(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header == null || !header.startsWith(BASIC_PREFIX)) {
            log.warn("Rejected request with missing/invalid Authorization header: {} {}",
                    request.getMethod(), request.getRequestURI());
            throw new AuthenticationException(
                    "Missing or invalid Authorization header; expected HTTP Basic credentials");
        }

        String decoded;
        try {
            byte[] raw = Base64.getDecoder().decode(header.substring(BASIC_PREFIX.length()).trim());
            decoded = new String(raw, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException("Malformed Authorization header");
        }

        int separatorIndex = decoded.indexOf(':');
        if (separatorIndex < 0) {
            throw new AuthenticationException("Malformed Authorization header");
        }

        String username = decoded.substring(0, separatorIndex);
        String password = decoded.substring(separatorIndex + 1);
        return new String[] {username, password};
    }
}
