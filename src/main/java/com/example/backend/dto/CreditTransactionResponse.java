package com.example.backend.dto;

import com.example.backend.entity.CreditTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreditTransactionResponse(
        String id,
        String userId,
        String userName,
        String type,
        BigDecimal amount,
        String description,
        LocalDateTime createdAt
) {

    public static CreditTransactionResponse from(CreditTransaction t) {
        return new CreditTransactionResponse(
                String.valueOf(t.getId()),
                String.valueOf(t.getUser().getId()),
                t.getUser().getFullName(),
                t.getType().name().toLowerCase(),
                t.getAmount(),
                t.getDescription(),
                t.getCreatedAt());
    }
}
