package com.gym.crm.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class RestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RestLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(LoggingConstants.REQUEST_START_TIME_ATTRIBUTE, System.currentTimeMillis());
        log.info("REST call received: {} {}", request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        long startTime = (long) request.getAttribute(LoggingConstants.REQUEST_START_TIME_ATTRIBUTE);
        long executionTimeMs = System.currentTimeMillis() - startTime;

        if (ex != null) {
            log.warn("REST call failed: {} {} status={} executionTimeMs={} error={}",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), executionTimeMs,
                    ex.getMessage());
        } else {
            log.info("REST call completed: {} {} status={} executionTimeMs={}",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), executionTimeMs);
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
    }
}