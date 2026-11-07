package com.example.backend.dto;

import com.example.backend.entity.Notification;

import java.time.LocalDateTime;
import java.util.Map;

public record NotificationResponse(
        String id,
        String userId,
        String type,
        Map<String, String> title,
        Map<String, String> body,
        boolean read,
        LocalDateTime createdAt
) {

    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                String.valueOf(n.getId()),
                String.valueOf(n.getUser().getId()),
                n.getType(),
                n.getTitle(),
                n.getBody(),
                Boolean.TRUE.equals(n.getRead()),
                n.getCreatedAt());
    }
}
