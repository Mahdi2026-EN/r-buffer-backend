package com.example.backend.service;

import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.entity.UserStatus;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public AdminService(UserRepository userRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // All accounts, newest first
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    // Accounts waiting for approval
    @Transactional(readOnly = true)
    public List<User> getPendingUsers() {
        return userRepository.findByStatus(UserStatus.PENDING);
    }

    @Transactional
    public User approveUser(Long userId) {
        User user = findManagedUser(userId);
        requireStatus(user, "Only pending users can be approved", UserStatus.PENDING);
        user.setStatus(UserStatus.APPROVED);
        notificationService.notify(user, "account", "Account approved",
                "Your account has been approved. You can now sign in and shop.");
        return userRepository.save(user);
    }

    @Transactional
    public User rejectUser(Long userId) {
        User user = findManagedUser(userId);
        requireStatus(user, "Only pending users can be rejected", UserStatus.PENDING);
        user.setStatus(UserStatus.REJECTED);
        notificationService.notify(user, "account", "Registration rejected",
                "Your registration was rejected. Please contact support for details.");
        return userRepository.save(user);
    }

    @Transactional
    public User suspendUser(Long userId) {
        User user = findManagedUser(userId);
        requireStatus(user, "Only approved users can be suspended", UserStatus.APPROVED);
        user.setStatus(UserStatus.SUSPENDED);
        notificationService.notify(user, "account", "Account suspended",
                "Your account has been suspended. Please contact support.");
        return userRepository.save(user);
    }

    @Transactional
    public User activateUser(Long userId) {
        User user = findManagedUser(userId);
        requireStatus(user, "This user is already active",
                UserStatus.PENDING, UserStatus.REJECTED, UserStatus.SUSPENDED);
        user.setStatus(UserStatus.APPROVED);
        notificationService.notify(user, "account", "Account activated",
                "Your account is active again.");
        return userRepository.save(user);
    }

    // Admin accounts cannot be changed from the panel, so an admin can never lock another admin out
    private User findManagedUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "USER_NOT_FOUND", "User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ADMIN_PROTECTED",
                    "Administrator accounts cannot be changed here");
        }

        return user;
    }

    private void requireStatus(User user, String message, UserStatus... allowed) {
        for (UserStatus status : allowed) {
            if (user.getStatus() == status) {
                return;
            }
        }
        throw new ApiException(HttpStatus.CONFLICT, "INVALID_STATUS_CHANGE", message);
    }
}
