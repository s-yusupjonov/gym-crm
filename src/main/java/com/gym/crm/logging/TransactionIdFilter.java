package com.gym.crm.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

public class TransactionIdFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(TransactionIdFilter.class);

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String transactionId = resolveTransactionId(request);
        MDC.put(LoggingConstants.TRANSACTION_ID_MDC_KEY, transactionId);
        response.setHeader(LoggingConstants.TRANSACTION_ID_HEADER, transactionId);

        long start = System.currentTimeMillis();
        log.info("Transaction started: {} {}", request.getMethod(), request.getRequestURI());
        try {
            chain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - start;
            log.info("Transaction finished: {} {} status={} durationMs={}",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), durationMs);
            MDC.remove(LoggingConstants.TRANSACTION_ID_MDC_KEY);
        }
    }

    private String resolveTransactionId(HttpServletRequest request) {
        String incoming = request.getHeader(LoggingConstants.TRANSACTION_ID_HEADER);
        return (incoming != null && !incoming.isBlank()) ? incoming : UUID.randomUUID().toString();
    }
}