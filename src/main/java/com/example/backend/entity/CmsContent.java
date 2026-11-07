package com.example.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

// Editable website text (footer, about, FAQ text, ...), in English, Dari and Pashto.
@Entity
@Table(name = "cms_content",
        uniqueConstraints = @UniqueConstraint(columnNames = {"page", "section", "content_key"}))
public class CmsContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String page;

    @Column(nullable = false, length = 60)
    private String section;

    @Column(name = "content_key", nullable = false, length = 80)
    private String contentKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content_value", nullable = false)
    private Map<String, String> value;

    public CmsContent() {
    }

    public CmsContent(String page, String section, String contentKey, Map<String, String> value) {
        this.page = page;
        this.section = section;
        this.contentKey = contentKey;
        this.value = value;
    }

    public Long getId() { return id; }
    public String getPage() { return page; }
    public String getSection() { return section; }
    public String getContentKey() { return contentKey; }
    public Map<String, String> getValue() { return value; }
    public void setValue(Map<String, String> value) { this.value = value; }
}
