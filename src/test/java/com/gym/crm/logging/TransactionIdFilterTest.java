package com.gym.crm.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionIdFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    private TransactionIdFilter filter;

    @BeforeEach
    void setUp() {
        filter = new TransactionIdFilter();
        lenient().when(request.getMethod()).thenReturn("GET");
        lenient().when(request.getRequestURI()).thenReturn("/api/trainees/john.doe");
        MDC.remove(LoggingConstants.TRANSACTION_ID_MDC_KEY);
    }

    @Test
    void doFilterShouldGenerateTransactionIdWhenHeaderMissing() throws Exception {
        when(request.getHeader(LoggingConstants.TRANSACTION_ID_HEADER)).thenReturn(null);

        filter.doFilter(request, response, chain);

        ArgumentCaptor<String> headerValue = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(org.mockito.ArgumentMatchers.eq(LoggingConstants.TRANSACTION_ID_HEADER),
                headerValue.capture());
        assertTrue(headerValue.getValue().length() > 0);
        verify(chain).doFilter(request, response);
        assertNull(MDC.get(LoggingConstants.TRANSACTION_ID_MDC_KEY));
    }

    @Test
    void doFilterShouldReuseIncomingTransactionIdHeader() throws Exception {
        when(request.getHeader(LoggingConstants.TRANSACTION_ID_HEADER)).thenReturn("incoming-id");

        filter.doFilter(request, response, chain);

        verify(response).setHeader(LoggingConstants.TRANSACTION_ID_HEADER, "incoming-id");
    }

    @Test
    void doFilterShouldGenerateTransactionIdWhenHeaderIsBlank() throws Exception {
        when(request.getHeader(LoggingConstants.TRANSACTION_ID_HEADER)).thenReturn("   ");

        filter.doFilter(request, response, chain);

        verify(response).setHeader(org.mockito.ArgumentMatchers.eq(LoggingConstants.TRANSACTION_ID_HEADER),
                org.mockito.ArgumentMatchers.argThat(value -> !value.isBlank() && !value.equals("   ")));
    }

    @Test
    void doFilterShouldClearMdcEvenWhenChainThrows() throws Exception {
        when(request.getHeader(LoggingConstants.TRANSACTION_ID_HEADER)).thenReturn(null);
        doThrow(new RuntimeException("downstream failure")).when(chain).doFilter(request, response);

        assertThrows(RuntimeException.class, () -> filter.doFilter(request, response, chain));

        assertNull(MDC.get(LoggingConstants.TRANSACTION_ID_MDC_KEY));
    }
}