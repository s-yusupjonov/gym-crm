package com.gym.crm.exception;

import com.gym.crm.controller.TrainingController;
import com.gym.crm.dto.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        lenient().when(request.getMethod()).thenReturn("GET");
        lenient().when(request.getRequestURI()).thenReturn("/api/trainees/john.doe");
    }

    @Test
    void handleAuthenticationShouldReturnUnauthorized() {
        ResponseEntity<ErrorResponse> response =
                handler.handleAuthentication(new AuthenticationException("Invalid username or password"), request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody().getMessage());
        assertEquals("/api/trainees/john.doe", response.getBody().getPath());
    }

    @Test
    void handleAccountLockedShouldReturnLocked() {
        ResponseEntity<ErrorResponse> response = handler.handleAccountLocked(
                new AccountLockedException("Account is temporarily locked due to too many failed login attempts"),
                request);

        assertEquals(HttpStatus.LOCKED, response.getStatusCode());
        assertEquals("Account is temporarily locked due to too many failed login attempts",
                response.getBody().getMessage());
    }

    @Test
    void handleNotFoundShouldReturnNotFound() {
        ResponseEntity<ErrorResponse> response =
                handler.handleNotFound(new EntityNotFoundException("Trainee not found: john.doe"), request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Trainee not found: john.doe", response.getBody().getMessage());
    }

    @Test
    void handleValidationShouldReturnBadRequest() {
        ResponseEntity<ErrorResponse> response =
                handler.handleValidation(new ValidationException("First name is required"), request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("First name is required", response.getBody().getMessage());
    }

    @Test
    void handleConstraintViolationShouldReturnBadRequestWithEmptyFieldErrors() {
        ConstraintViolationException ex = new ConstraintViolationException(Set.of());

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertEquals(0, response.getBody().getFieldErrors().size());
    }

    @Test
    void handleMissingParameterShouldReturnBadRequest() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("username", "String");

        ResponseEntity<ErrorResponse> response = handler.handleMissingParameter(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleTypeMismatchShouldReturnBadRequestNamingTheParameter() throws NoSuchMethodException {
        Method getTraineeTrainings = TrainingController.class.getMethod("getTraineeTrainings",
                String.class, LocalDate.class, LocalDate.class, String.class, String.class);
        MethodParameter periodFromParam = new MethodParameter(getTraineeTrainings, 1);

        org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex =
                new org.springframework.web.method.annotation.MethodArgumentTypeMismatchException(
                        "abc", LocalDate.class, "periodFrom", periodFromParam, new IllegalArgumentException());

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Parameter 'periodFrom' has an invalid value", response.getBody().getMessage());
    }

    @Test
    void handleUnreadableBodyShouldReturnBadRequest() {
        org.springframework.http.converter.HttpMessageNotReadableException ex =
                new org.springframework.http.converter.HttpMessageNotReadableException("broken json",
                        (org.springframework.http.HttpInputMessage) null);

        ResponseEntity<ErrorResponse> response = handler.handleUnreadableBody(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Malformed request body", response.getBody().getMessage());
    }

    @Test
    void handleMethodNotSupportedShouldReturnMethodNotAllowed() {
        when(request.getMethod()).thenReturn("DELETE");
        HttpRequestMethodNotSupportedException ex =
                new HttpRequestMethodNotSupportedException("DELETE", Set.of(HttpMethod.GET.name()));

        ResponseEntity<ErrorResponse> response = handler.handleMethodNotSupported(ex, request);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    }

    @Test
    void handleUnexpectedShouldReturnInternalServerErrorWithGenericMessage() {
        ResponseEntity<ErrorResponse> response = handler.handleUnexpected(new RuntimeException("boom"), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    void errorResponseShouldOmitTransactionIdWhenNotSetInMdc() {
        ResponseEntity<ErrorResponse> response =
                handler.handleNotFound(new EntityNotFoundException("Trainee not found: john.doe"), request);

        assertNull(response.getBody().getTransactionId());
    }
}