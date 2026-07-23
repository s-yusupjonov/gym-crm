package com.gym.crm.logging;

public final class LoggingConstants {

    public static final String TRANSACTION_ID_MDC_KEY = "transactionId";

    public static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    public static final String REQUEST_START_TIME_ATTRIBUTE = "requestStartTimeMillis";

    private LoggingConstants() {
    }
}