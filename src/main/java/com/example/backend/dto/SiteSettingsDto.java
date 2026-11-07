package com.example.backend.dto;

import com.example.backend.entity.SiteSettings;

import java.util.Arrays;
import java.util.List;

public record SiteSettingsDto(
        String siteName,
        String contactEmail,
        String contactPhone,
        String defaultLanguage,
        List<String> supportedLanguages,
        String theme,
        boolean enableNotifications
) {

    public static SiteSettingsDto from(SiteSettings s) {
        return new SiteSettingsDto(
                s.getSiteName(),
                s.getContactEmail(),
                s.getContactPhone(),
                s.getDefaultLanguage(),
                Arrays.stream(s.getSupportedLanguages().split(",")).map(String::trim).filter(x -> !x.isEmpty()).toList(),
                s.getTheme(),
                Boolean.TRUE.equals(s.getEnableNotifications()));
    }
}
