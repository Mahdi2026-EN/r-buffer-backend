package com.example.backend.dto;

import com.example.backend.entity.PaymentRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRequestResponse(
        String id,
        String userId,
        String userName,
        String userEmail,
        BigDecimal amount,
        String method,
        String reference,
        String note,
        String status,
        String adminNote,
        LocalDateTime createdAt,
        LocalDateTime reviewedAt
) {

    public static PaymentRequestResponse from(PaymentRequest p) {
        return new PaymentRequestResponse(
                String.valueOf(p.getId()),
                String.valueOf(p.getUser().getId()),
                p.getUser().getFullName(),
                p.getUser().getEmail(),
                p.getAmount(),
                p.getMethod().name().toLowerCase(),
                p.getReference(),
                p.getNote(),
                p.getStatus().name().toLowerCase(),
                p.getAdminNote(),
                p.getCreatedAt(),
                p.getReviewedAt());
    }
}
