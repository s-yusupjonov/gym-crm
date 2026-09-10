package com.gym.crm.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestAccessDeniedHandlerTest {

    @Mock
    private HttpServletRequest request;

    private RestAccessDeniedHandler accessDeniedHandler;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        accessDeniedHandler = new RestAccessDeniedHandler(new ObjectMapper());
        response = new MockHttpServletResponse();
    }

    @Test
    void handleShouldWriteForbiddenJsonBody() throws Exception {
        when(request.getRequestURI()).thenReturn("/gym-crm/api/trainers/carl.coach");

        accessDeniedHandler.handle(request, response, new AccessDeniedException("denied"));

        assertEquals(403, response.getStatus());
        assertEquals("application/json", response.getContentType());
        assertTrue(response.getContentAsString().contains("Access is denied"));
        assertTrue(response.getContentAsString().contains("/gym-crm/api/trainers/carl.coach"));
    }
}