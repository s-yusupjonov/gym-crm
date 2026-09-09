package com.gym.crm.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Test
    void commenceShouldWriteUnauthorizedJsonBody() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(request.getRequestURI()).thenReturn("/gym-crm/api/trainees/john.doe");

        RestAuthenticationEntryPoint.commence(request, response);

        assertEquals(401, response.getStatus());
        assertEquals("application/json", response.getContentType());
        assertEquals(true, response.getContentAsString().contains("Authentication is required"));
        assertEquals(true, response.getContentAsString().contains("/gym-crm/api/trainees/john.doe"));
    }
}