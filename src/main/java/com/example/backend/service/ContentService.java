package com.example.backend.service;

import com.example.backend.dto.AnnouncementRequest;
import com.example.backend.dto.AnnouncementResponse;
import com.example.backend.dto.CmsContentRequest;
import com.example.backend.dto.CmsContentResponse;
import com.example.backend.entity.Announcement;
import com.example.backend.entity.CmsContent;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.AnnouncementRepository;
import com.example.backend.repository.CmsContentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class ContentService {

    private static final Set<String> LANGUAGES = Set.of("all", "en", "fa", "ps");

    private final CmsContentRepository cmsContentRepository;
    private final AnnouncementRepository announcementRepository;

    public ContentService(CmsContentRepository cmsContentRepository, AnnouncementRepository announcementRepository) {
        this.cmsContentRepository = cmsContentRepository;
        this.announcementRepository = announcementRepository;
    }

    // ---------- CMS text ----------

    @Transactional(readOnly = true)
    public List<CmsContentResponse> listContent() {
        return cmsContentRepository.findAllByOrderByPageAscSectionAscContentKeyAsc()
                .stream().map(CmsContentResponse::from).toList();
    }

    @Transactional
    public CmsContentResponse updateContent(Long id, CmsContentRequest request) {
        CmsContent content = cmsContentRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Content not found"));
        content.setValue(request.value());
        return CmsContentResponse.from(content);
    }

    // ---------- Announcements ----------

    @Transactional(readOnly = true)
    public List<AnnouncementResponse> listPublished() {
        return announcementRepository.findByPublishedTrueOrderByCreatedAtDesc()
                .stream().map(AnnouncementResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AnnouncementResponse> listAll() {
        return announcementRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(AnnouncementResponse::from).toList();
    }

    @Transactional
    public AnnouncementResponse createAnnouncement(AnnouncementRequest request) {
        Announcement a = new Announcement();
        apply(a, request);
        return AnnouncementResponse.from(announcementRepository.save(a));
    }

    @Transactional
    public AnnouncementResponse updateAnnouncement(Long id, AnnouncementRequest request) {
        Announcement a = announcementRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Announcement not found"));
        apply(a, request);
        return AnnouncementResponse.from(a);
    }

    @Transactional
    public void deleteAnnouncement(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Announcement not found");
        }
        announcementRepository.deleteById(id);
    }

    private void apply(Announcement a, AnnouncementRequest request) {
        if (request.title() == null || request.title().get("en") == null || request.title().get("en").isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "An English title is required");
        }
        String language = request.language() == null ? "all" : request.language();
        if (!LANGUAGES.contains(language)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Invalid language");
        }
        a.setTitle(request.title());
        a.setBody(request.body());
        a.setPublished(request.published() == null || request.published());
        a.setLanguage(language);
    }
}
