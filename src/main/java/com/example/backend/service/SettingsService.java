package com.example.backend.service;

import com.example.backend.dto.SiteSettingsDto;
import com.example.backend.entity.SiteSettings;
import com.example.backend.repository.SiteSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SettingsService {

    private final SiteSettingsRepository siteSettingsRepository;

    public SettingsService(SiteSettingsRepository siteSettingsRepository) {
        this.siteSettingsRepository = siteSettingsRepository;
    }

    @Transactional
    public SiteSettingsDto get() {
        return SiteSettingsDto.from(current());
    }

    @Transactional
    public SiteSettingsDto update(SiteSettingsDto dto) {
        SiteSettings s = current();
        s.setSiteName(dto.siteName().trim());
        s.setContactEmail(dto.contactEmail().trim());
        s.setContactPhone(dto.contactPhone().trim());
        s.setDefaultLanguage(dto.defaultLanguage());
        s.setSupportedLanguages(dto.supportedLanguages().stream().distinct()
                .collect(Collectors.joining(",")));
        s.setTheme(dto.theme());
        s.setEnableNotifications(dto.enableNotifications());
        return SiteSettingsDto.from(s);
    }

    // Creates the single settings row the first time it is needed
    private SiteSettings current() {
        return siteSettingsRepository.findById(SiteSettings.SINGLETON_ID)
                .orElseGet(() -> siteSettingsRepository.save(new SiteSettings()));
    }
}
