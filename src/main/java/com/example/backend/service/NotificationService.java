package com.example.backend.service;

import com.example.backend.dto.NotificationResponse;
import com.example.backend.entity.Notification;
import com.example.backend.entity.User;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // Creates a notification. Until translations are added on the server, the same English text is stored for every language.
    @Transactional
    public void notify(User user, String type, String title, String body) {
        notificationRepository.save(new Notification(user, type, Map.of(
                "en", title, "fa", title, "ps", title), Map.of(
                "en", body, "fa", body, "ps", body)));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listMine(String email) {
        User user = currentUser(email);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(NotificationResponse::from).toList();
    }

    @Transactional
    public void markRead(String email, Long id) {
        User user = currentUser(email);
        Notification n = notificationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Notification not found"));
        n.setRead(true);
    }

    @Transactional
    public void markAllRead(String email) {
        User user = currentUser(email);
        notificationRepository.findByUserIdAndReadFalse(user.getId()).forEach(n -> n.setRead(true));
    }

    private User currentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
    }
}
