package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record CmsContentRequest(
        @NotNull Map<String, String> value
) {
}
