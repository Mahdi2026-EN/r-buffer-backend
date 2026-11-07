package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderStatusRequest(
        @NotBlank String status,
        String note
) {
}
