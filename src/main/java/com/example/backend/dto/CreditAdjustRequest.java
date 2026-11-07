package com.example.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreditAdjustRequest(
        @NotBlank String type,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        String note
) {
}
