package com.gym.crm.security;

import com.gym.crm.exception.AuthenticationException;
import com.gym.crm.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationInterceptorTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private AuthenticationInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new AuthenticationInterceptor(authenticationService);
        lenient().when(request.getMethod()).thenReturn("GET");
    }

    @Test
    void shouldAllowTraineeRegistrationWithoutCredentials() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getServletPath()).thenReturn("/api/trainees");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verify(authenticationService, never()).authenticate(anyString(), anyString());
    }

    @Test
    void shouldAllowTrainerRegistrationWithoutCredentials() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getServletPath()).thenReturn("/api/trainers");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verify(authenticationService, never()).authenticate(anyString(), anyString());
    }

    @Test
    void shouldRequireCredentialsForPostToOtherPaths() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getServletPath()).thenReturn("/api/trainings");
        when(request.getHeader("Authorization")).thenReturn(basicHeader("John.Smith", "pw"));

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verify(authenticationService).authenticate("John.Smith", "pw");
    }

    @Test
    void shouldNotTreatGetOnTraineesPathAsRegistration() {
        // GET is not POST, so isRegistrationRequest short-circuits on method alone - path is never consulted.
        when(request.getHeader("Authorization")).thenReturn(basicHeader("John.Smith", "pw"));

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verify(authenticationService).authenticate("John.Smith", "pw");
    }

    @Test
    void shouldAllowOptionsRequestsWithoutCredentials() {
        when(request.getMethod()).thenReturn("OPTIONS");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verify(authenticationService, never()).authenticate(anyString(), anyString());
    }

    @Test
    void shouldRejectRequestWithMissingAuthorizationHeader() {
        when(request.getHeader("Authorization")).thenReturn(null);

        assertThrows(AuthenticationException.class,
                () -> interceptor.preHandle(request, response, new Object()));
        verify(authenticationService, never()).authenticate(anyString(), anyString());
    }

    @Test
    void shouldRejectRequestWithNonBasicAuthorizationHeader() {
        when(request.getHeader("Authorization")).thenReturn("Bearer sometoken");

        assertThrows(AuthenticationException.class,
                () -> interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void shouldRejectMalformedBase64Credentials() {
        when(request.getHeader("Authorization")).thenReturn("Basic not-valid-base64!!");

        assertThrows(AuthenticationException.class,
                () -> interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void shouldRejectCredentialsWithoutColonSeparator() {
        String encoded = Base64.getEncoder().encodeToString("nocolonhere".getBytes(StandardCharsets.UTF_8));
        when(request.getHeader("Authorization")).thenReturn("Basic " + encoded);

        assertThrows(AuthenticationException.class,
                () -> interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void shouldAuthenticateAndAllowRequestWithValidBasicCredentials() {
        when(request.getHeader("Authorization")).thenReturn(basicHeader("John.Smith", "s3cret"));

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verify(authenticationService).authenticate("John.Smith", "s3cret");
    }

    @Test
    void shouldPropagateAuthenticationExceptionFromService() {
        when(request.getHeader("Authorization")).thenReturn(basicHeader("John.Smith", "wrong"));
        doThrow(new AuthenticationException("Invalid username or password"))
                .when(authenticationService).authenticate("John.Smith", "wrong");

        assertThrows(AuthenticationException.class,
                () -> interceptor.preHandle(request, response, new Object()));
    }

    private static String basicHeader(String username, String password) {
        String raw = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
}
