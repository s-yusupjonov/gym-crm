package com.gym.crm.util;

import com.gym.crm.exception.ValidationException;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static void requireNonBlank(String value, String errorMessage) {
        if (isBlank(value)) {
            throw new ValidationException(errorMessage);
        }
    }
}