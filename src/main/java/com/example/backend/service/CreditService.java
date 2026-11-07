package com.example.backend.service;

import com.example.backend.dto.CreditTransactionResponse;
import com.example.backend.entity.CreditTransaction;
import com.example.backend.entity.CreditTransactionType;
import com.example.backend.entity.User;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.CreditTransactionRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CreditService {

    private final CreditTransactionRepository creditTransactionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public CreditService(CreditTransactionRepository creditTransactionRepository,
                         UserRepository userRepository,
                         NotificationService notificationService) {
        this.creditTransactionRepository = creditTransactionRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<CreditTransactionResponse> listMine(String email) {
        User user = userByEmail(email);
        return creditTransactionRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(CreditTransactionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<CreditTransactionResponse> listAll() {
        return creditTransactionRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(CreditTransactionResponse::from).toList();
    }

    // Admin adds or removes credit for a user. Every change is logged in the ledger.
    @Transactional
    public void adjust(Long userId, String typeText, BigDecimal amount, String note) {
        CreditAdjust type = parseAdjust(typeText);
        User user = lockUser(userId);

        if (type == CreditAdjust.REMOVE) {
            if (user.getCredit().compareTo(amount) < 0) {
                throw new ApiException(HttpStatus.CONFLICT, "INSUFFICIENT_CREDIT",
                        "The user does not have enough credit to remove that amount");
            }
            user.setCredit(user.getCredit().subtract(amount));
            ledger(user, CreditTransactionType.USED, amount, "Admin debit" + suffix(note));
            notificationService.notify(user, "credit", "Credit adjusted",
                    "An administrator removed " + amount.stripTrailingZeros().toPlainString() + " credit from your account.");
        } else {
            user.setCredit(user.getCredit().add(amount));
            ledger(user, CreditTransactionType.ADDED, amount, "Admin credit" + suffix(note));
            notificationService.notify(user, "credit", "Credit added",
                    "An administrator added " + amount.stripTrailingZeros().toPlainString() + " credit to your account.");
        }
    }

    // Used by orders and payment approvals
    @Transactional
    public void ledger(User user, CreditTransactionType type, BigDecimal amount, String description) {
        creditTransactionRepository.save(new CreditTransaction(user, type, amount, description));
    }

    // Lock a user row before changing its credit
    @Transactional
    public User lockUser(Long userId) {
        return userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
    }

    private User userByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
    }

    private static String suffix(String note) {
        return note == null || note.isBlank() ? "" : " — " + note.trim();
    }

    private enum CreditAdjust { ADD, REMOVE }

    private static CreditAdjust parseAdjust(String value) {
        if ("add".equalsIgnoreCase(value)) return CreditAdjust.ADD;
        if ("remove".equalsIgnoreCase(value)) return CreditAdjust.REMOVE;
        throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_ADJUSTMENT", "Type must be add or remove");
    }
}
