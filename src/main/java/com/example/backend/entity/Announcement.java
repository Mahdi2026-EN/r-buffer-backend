package com.example.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private Map<String, String> title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private Map<String, String> body;

    @Column(nullable = false)
    private Boolean published = true;

    // "all", "en", "fa" or "ps"
    @Column(nullable = false, length = 10)
    private String language = "all";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Announcement() {
    }

    public Long getId() { return id; }
    public Map<String, String> getTitle() { return title; }
    public void setTitle(Map<String, String> title) { this.title = title; }
    public Map<String, String> getBody() { return body; }
    public void setBody(Map<String, String> body) { this.body = body; }
    public Boolean getPublished() { return published; }
    public void setPublished(Boolean published) { this.published = published; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (published == null) published = true;
        if (language == null) language = "all";
    }
}
