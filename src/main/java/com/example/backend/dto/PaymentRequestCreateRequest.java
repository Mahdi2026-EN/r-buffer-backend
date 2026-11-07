package com.example.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PaymentRequestCreateRequest(
        @NotNull @DecimalMin(value = "10", message = "Minimum amount is 10") BigDecimal amount,
        @NotBlank String method,
        @NotBlank @Size(max = 150) String reference,
        @Size(max = 1000) String note
) {
}
