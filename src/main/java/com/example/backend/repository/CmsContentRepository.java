package com.example.backend.repository;

import com.example.backend.entity.CmsContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CmsContentRepository extends JpaRepository<CmsContent, Long> {

    List<CmsContent> findAllByOrderByPageAscSectionAscContentKeyAsc();
}
