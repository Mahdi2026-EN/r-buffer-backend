package com.example.backend.controller;

import com.example.backend.dto.AnnouncementRequest;
import com.example.backend.dto.AnnouncementResponse;
import com.example.backend.dto.CmsContentRequest;
import com.example.backend.dto.CmsContentResponse;
import com.example.backend.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Public read endpoints and admin write endpoints for website text and announcements
@RestController
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    // Public: published announcements
    @GetMapping("/api/announcements")
    public List<AnnouncementResponse> publicAnnouncements() {
        return contentService.listPublished();
    }

    // Public: all editable website text
    @GetMapping("/api/content")
    public List<CmsContentResponse> publicContent() {
        return contentService.listContent();
    }

    // Admin: all announcements, including drafts
    @GetMapping("/api/admin/announcements")
    public List<AnnouncementResponse> allAnnouncements() {
        return contentService.listAll();
    }

    @PostMapping("/api/admin/announcements")
    public ResponseEntity<AnnouncementResponse> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contentService.createAnnouncement(request));
    }

    @PutMapping("/api/admin/announcements/{id}")
    public AnnouncementResponse updateAnnouncement(@PathVariable Long id, @Valid @RequestBody AnnouncementRequest request) {
        return contentService.updateAnnouncement(id, request);
    }

    @DeleteMapping("/api/admin/announcements/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        contentService.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }

    // Admin: edit one piece of website text
    @PutMapping("/api/admin/content/{id}")
    public CmsContentResponse updateContent(@PathVariable Long id, @Valid @RequestBody CmsContentRequest request) {
        return contentService.updateContent(id, request);
    }
}
