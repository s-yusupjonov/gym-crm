package com.gym.crm.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    private RestAuthenticationEntryPoint entryPoint;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        entryPoint = new RestAuthenticationEntryPoint(new ObjectMapper());
        response = new MockHttpServletResponse();
    }

    @Test
    void commenceShouldWriteUnauthorizedJsonBody() throws Exception {
        when(request.getRequestURI()).thenReturn("/gym-crm/api/trainees/john.doe");

        entryPoint.commence(request, response, new BadCredentialsException("bad credentials"));

        assertEquals(401, response.getStatus());
        assertEquals("application/json", response.getContentType());
        assertEquals(true, response.getContentAsString().contains("Authentication is required"));
        assertEquals(true, response.getContentAsString().contains("/gym-crm/api/trainees/john.doe"));
    }
}