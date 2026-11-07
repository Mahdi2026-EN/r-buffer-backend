package com.example.backend.entity;

import jakarta.persistence.*;

// Single-row table: there is only ever one row with id = 1.
@Entity
@Table(name = "site_settings")
public class SiteSettings {

    public static final long SINGLETON_ID = 1L;

    @Id
    private Long id = SINGLETON_ID;

    @Column(nullable = false, length = 100)
    private String siteName = "R-Buffer";

    @Column(nullable = false, length = 150)
    private String contactEmail = "support@rbuffer.io";

    @Column(nullable = false, length = 30)
    private String contactPhone = "+93 700 000 000";

    @Column(nullable = false, length = 5)
    private String defaultLanguage = "en";

    // Comma-separated, e.g. "en,fa,ps"
    @Column(nullable = false, length = 50)
    private String supportedLanguages = "en,fa,ps";

    @Column(nullable = false, length = 10)
    private String theme = "light";

    @Column(nullable = false)
    private Boolean enableNotifications = true;

    public SiteSettings() {
    }

    public Long getId() { return id; }
    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getDefaultLanguage() { return defaultLanguage; }
    public void setDefaultLanguage(String defaultLanguage) { this.defaultLanguage = defaultLanguage; }
    public String getSupportedLanguages() { return supportedLanguages; }
    public void setSupportedLanguages(String supportedLanguages) { this.supportedLanguages = supportedLanguages; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    public Boolean getEnableNotifications() { return enableNotifications; }
    public void setEnableNotifications(Boolean enableNotifications) { this.enableNotifications = enableNotifications; }
}
