package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.controller.support.MockMvcTestSupport;
import com.gym.crm.dto.auth.ChangeLoginRequest;
import com.gym.crm.exception.AuthenticationException;
import com.gym.crm.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = MockMvcTestSupport.objectMapper();
        AuthenticationController controller = MockMvcTestSupport.withMethodValidation(
                new AuthenticationController(authenticationService), AuthenticationController.class);
        mockMvc = MockMvcTestSupport.mockMvc(controller);
    }

    @Test
    void loginShouldReturnOkWhenCredentialsAreValid() throws Exception {
        mockMvc.perform(get("/api/login")
                        .param("username", "john.doe")
                        .param("password", "secret"))
                .andExpect(status().isOk());

        verify(authenticationService).authenticate("john.doe", "secret");
    }

    @Test
    void loginShouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        doThrow(new AuthenticationException("Invalid username or password"))
                .when(authenticationService).authenticate("john.doe", "wrong");

        mockMvc.perform(get("/api/login")
                        .param("username", "john.doe")
                        .param("password", "wrong"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginShouldReturnBadRequestWhenUsernameIsMissing() throws Exception {
        mockMvc.perform(get("/api/login")
                        .param("password", "secret"))
                .andExpect(status().isBadRequest());

        verify(authenticationService, never()).authenticate(eq("john.doe"), eq("secret"));
    }

    @Test
    void loginShouldReturnBadRequestWhenUsernameIsBlank() throws Exception {
        mockMvc.perform(get("/api/login")
                        .param("username", "")
                        .param("password", "secret"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeLoginShouldReturnOkWhenValid() throws Exception {
        ChangeLoginRequest request = new ChangeLoginRequest("john.doe", "oldPass", "newPass");

        mockMvc.perform(put("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(authenticationService).changePassword("john.doe", "oldPass", "newPass");
    }

    @Test
    void changeLoginShouldReturnBadRequestWhenNewPasswordIsMissing() throws Exception {
        ChangeLoginRequest request = new ChangeLoginRequest("john.doe", "oldPass", null);

        mockMvc.perform(put("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authenticationService, never()).changePassword(eq("john.doe"), eq("oldPass"), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void changeLoginShouldReturnUnauthorizedWhenOldCredentialsAreInvalid() throws Exception {
        ChangeLoginRequest request = new ChangeLoginRequest("john.doe", "wrongOld", "newPass");
        doThrow(new AuthenticationException("Invalid username or password"))
                .when(authenticationService).changePassword("john.doe", "wrongOld", "newPass");

        mockMvc.perform(put("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changeLoginShouldReturnBadRequestWhenNewPasswordIsBlank() throws Exception {
        ChangeLoginRequest request = new ChangeLoginRequest("john.doe", "oldPass", "   ");

        mockMvc.perform(put("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authenticationService, never()).changePassword(eq("john.doe"), eq("oldPass"), org.mockito.ArgumentMatchers.any());
    }
}