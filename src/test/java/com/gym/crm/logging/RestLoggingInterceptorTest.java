package com.gym.crm.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestLoggingInterceptorTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private RestLoggingInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new RestLoggingInterceptor();
        lenient().when(request.getMethod()).thenReturn("GET");
        lenient().when(request.getRequestURI()).thenReturn("/api/trainees/john.doe");
    }

    @Test
    void preHandleShouldStoreStartTimeAndReturnTrue() {
        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verify(request).setAttribute(org.mockito.ArgumentMatchers.eq(LoggingConstants.REQUEST_START_TIME_ATTRIBUTE),
                org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void afterCompletionShouldNotThrowWhenExceptionIsNull() {
        when(request.getAttribute(LoggingConstants.REQUEST_START_TIME_ATTRIBUTE))
                .thenReturn(System.currentTimeMillis() - 5);
        when(response.getStatus()).thenReturn(200);

        interceptor.afterCompletion(request, response, new Object(), null);
    }

    @Test
    void afterCompletionShouldNotThrowWhenExceptionIsPresent() {
        when(request.getAttribute(LoggingConstants.REQUEST_START_TIME_ATTRIBUTE))
                .thenReturn(System.currentTimeMillis() - 5);
        when(response.getStatus()).thenReturn(500);

        interceptor.afterCompletion(request, response, new Object(), new RuntimeException("boom"));
    }

    @Test
    void postHandleShouldNotThrow() {
        interceptor.postHandle(request, response, new Object(), null);
    }
}