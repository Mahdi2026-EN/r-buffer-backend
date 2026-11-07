package com.example.backend.controller;

import com.example.backend.dto.SiteSettingsDto;
import com.example.backend.service.SettingsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    // Public: site name, contact details, languages
    @GetMapping("/api/settings")
    public SiteSettingsDto publicSettings() {
        return settingsService.get();
    }

    // Admin: save site settings
    @PutMapping("/api/admin/settings")
    public SiteSettingsDto update(@Valid @RequestBody SiteSettingsDto request) {
        return settingsService.update(request);
    }
}
