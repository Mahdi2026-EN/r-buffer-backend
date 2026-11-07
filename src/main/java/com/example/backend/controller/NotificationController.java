package com.example.backend.controller;

import com.example.backend.dto.NotificationResponse;
import com.example.backend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/mine")
    public List<NotificationResponse> mine(Authentication auth) {
        return notificationService.listMine(auth.getName());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(Authentication auth, @PathVariable Long id) {
        notificationService.markRead(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead(Authentication auth) {
        notificationService.markAllRead(auth.getName());
        return ResponseEntity.noContent().build();
    }
}
