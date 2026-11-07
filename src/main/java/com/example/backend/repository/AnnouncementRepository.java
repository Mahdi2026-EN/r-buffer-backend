package com.example.backend.repository;

import com.example.backend.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByPublishedTrueOrderByCreatedAtDesc();

    List<Announcement> findAllByOrderByCreatedAtDesc();
}
