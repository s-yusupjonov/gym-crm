package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.controller.support.MockMvcTestSupport;
import com.gym.crm.dto.auth.ChangeLoginRequest;
import com.gym.crm.dto.auth.LoginRequest;
import com.gym.crm.exception.AccountLockedException;
import com.gym.crm.exception.AuthenticationException;
import com.gym.crm.security.JwtService;
import com.gym.crm.security.TokenBlacklistService;
import com.gym.crm.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = MockMvcTestSupport.objectMapper();
        AuthenticationController controller = MockMvcTestSupport.withMethodValidation(
                new AuthenticationController(authenticationService, jwtService, tokenBlacklistService),
                AuthenticationController.class);
        mockMvc = MockMvcTestSupport.mockMvc(controller);
    }

    @Test
    void loginShouldReturnOkWithTokenWhenCredentialsAreValid() throws Exception {
        when(authenticationService.login("john.doe", "secret")).thenReturn("jwt-token");

        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new LoginRequest("john.doe", "secret"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void loginShouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        when(authenticationService.login("john.doe", "wrong"))
                .thenThrow(new AuthenticationException("Invalid username or password"));

        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new LoginRequest("john.doe", "wrong"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginShouldReturnLockedWhenAccountIsLocked() throws Exception {
        when(authenticationService.login("john.doe", "secret"))
                .thenThrow(new AccountLockedException("Account is temporarily locked due to too many failed "
                        + "login attempts"));

        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new LoginRequest("john.doe", "secret"))))
                .andExpect(status().isLocked());
    }

    @Test
    void loginShouldReturnBadRequestWhenUsernameIsMissing() throws Exception {
        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new LoginRequest(null, "secret"))))
                .andExpect(status().isBadRequest());

        verify(authenticationService, never()).login(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void loginShouldReturnBadRequestWhenPasswordIsMissing() throws Exception {
        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new LoginRequest("john.doe", null))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeLoginShouldReturnOkWhenRequestIsValid() throws Exception {
        ChangeLoginRequest request = new ChangeLoginRequest("john.doe", "oldPass", "newPass");

        mockMvc.perform(put("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(authenticationService).changePassword("john.doe", "oldPass", "newPass");
    }

    @Test
    void changeLoginShouldReturnUnauthorizedWhenOldCredentialsAreInvalid() throws Exception {
        doThrow(new AuthenticationException("Invalid username or password"))
                .when(authenticationService).changePassword("john.doe", "wrongOld", "newPass");
        ChangeLoginRequest request = new ChangeLoginRequest("john.doe", "wrongOld", "newPass");

        mockMvc.perform(put("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changeLoginShouldReturnBadRequestWhenNewPasswordIsBlank() throws Exception {
        ChangeLoginRequest request = new ChangeLoginRequest("john.doe", "oldPass", "");

        mockMvc.perform(put("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authenticationService, never()).changePassword(
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    void logoutShouldReturnOkAndBlacklistTokenWhenBearerTokenIsPresent() throws Exception {
        Date expiration = new Date();
        when(jwtService.extractJti("valid-token")).thenReturn("token-id");
        when(jwtService.extractExpiration("valid-token")).thenReturn(expiration);

        mockMvc.perform(post("/api/logout").header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk());

        verify(tokenBlacklistService).blacklist("token-id", expiration);
    }

    @Test
    void logoutShouldReturnUnauthorizedWhenAuthorizationHeaderIsMissing() throws Exception {
        mockMvc.perform(post("/api/logout"))
                .andExpect(status().isUnauthorized());

        verify(tokenBlacklistService, never()).blacklist(
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void logoutShouldReturnUnauthorizedWhenAuthorizationHeaderIsNotBearer() throws Exception {
        mockMvc.perform(post("/api/logout").header("Authorization", "Basic abc123"))
                .andExpect(status().isUnauthorized());

        verify(tokenBlacklistService, never()).blacklist(
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}