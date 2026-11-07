package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record AnnouncementRequest(
        @NotNull Map<String, String> title,
        @NotNull Map<String, String> body,
        Boolean published,
        String language
) {
}
