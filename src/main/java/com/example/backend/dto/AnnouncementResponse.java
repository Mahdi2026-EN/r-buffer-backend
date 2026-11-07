package com.example.backend.dto;

import com.example.backend.entity.Announcement;

import java.time.LocalDateTime;
import java.util.Map;

public record AnnouncementResponse(
        String id,
        Map<String, String> title,
        Map<String, String> body,
        boolean published,
        String language,
        LocalDateTime createdAt
) {

    public static AnnouncementResponse from(Announcement a) {
        return new AnnouncementResponse(
                String.valueOf(a.getId()),
                a.getTitle(),
                a.getBody(),
                Boolean.TRUE.equals(a.getPublished()),
                a.getLanguage(),
                a.getCreatedAt());
    }
}
