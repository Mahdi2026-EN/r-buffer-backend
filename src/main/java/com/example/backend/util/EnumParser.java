package com.example.backend.util;

import com.example.backend.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.Locale;

// Accepts "pending", "PENDING", " Pending " etc. from the front end.
public final class EnumParser {

    private EnumParser() {
    }

    public static <E extends Enum<E>> E parse(Class<E> type, String value, String code) {
        if (value == null || value.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, code, "A value is required");
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, code, "Invalid value: " + value);
        }
    }
}
