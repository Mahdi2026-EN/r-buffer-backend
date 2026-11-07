package com.example.backend.service;

import com.example.backend.dto.PaymentRequestCreateRequest;
import com.example.backend.dto.PaymentRequestResponse;
import com.example.backend.entity.CreditTransactionType;
import com.example.backend.entity.PaymentMethod;
import com.example.backend.entity.PaymentRequest;
import com.example.backend.entity.PaymentStatus;
import com.example.backend.entity.User;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.PaymentRequestRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.util.EnumParser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentRequestService {

    private final PaymentRequestRepository paymentRequestRepository;
    private final UserRepository userRepository;
    private final CreditService creditService;
    private final NotificationService notificationService;

    public PaymentRequestService(PaymentRequestRepository paymentRequestRepository,
                                 UserRepository userRepository,
                                 CreditService creditService,
                                 NotificationService notificationService) {
        this.paymentRequestRepository = paymentRequestRepository;
        this.userRepository = userRepository;
        this.creditService = creditService;
        this.notificationService = notificationService;
    }

    @Transactional
    public PaymentRequestResponse create(String email, PaymentRequestCreateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));

        PaymentRequest p = new PaymentRequest();
        p.setUser(user);
        p.setAmount(request.amount());
        p.setMethod(EnumParser.parse(PaymentMethod.class, request.method(), "INVALID_METHOD"));
        p.setReference(request.reference().trim());
        p.setNote(request.note() == null || request.note().isBlank() ? null : request.note().trim());
        p.setStatus(PaymentStatus.PENDING);

        PaymentRequest saved = paymentRequestRepository.save(p);
        notificationService.notify(user, "payment", "Payment request received",
                "We received your top-up request of " + request.amount().stripTrailingZeros().toPlainString() + " and will review it.");
        return PaymentRequestResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<PaymentRequestResponse> listMine(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
        return paymentRequestRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(PaymentRequestResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentRequestResponse> listAll() {
        return paymentRequestRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(PaymentRequestResponse::from).toList();
    }

    // Approving a top-up adds the amount to the user's credit and logs it in the ledger.
    @Transactional
    public PaymentRequestResponse approve(Long id) {
        PaymentRequest p = findPending(id);
        User user = creditService.lockUser(p.getUser().getId());

        user.setCredit(user.getCredit().add(p.getAmount()));
        creditService.ledger(user, CreditTransactionType.ADDED, p.getAmount(),
                "Top-up via " + p.getMethod().name().toLowerCase());

        p.setStatus(PaymentStatus.APPROVED);
        p.setReviewedAt(LocalDateTime.now());

        notificationService.notify(user, "payment", "Top-up approved",
                "Your top-up of " + p.getAmount().stripTrailingZeros().toPlainString() + " was approved and added to your credit.");
        return PaymentRequestResponse.from(p);
    }

    @Transactional
    public PaymentRequestResponse reject(Long id, String note) {
        PaymentRequest p = findPending(id);
        p.setStatus(PaymentStatus.REJECTED);
        p.setAdminNote(note == null || note.isBlank() ? null : note.trim());
        p.setReviewedAt(LocalDateTime.now());

        notificationService.notify(p.getUser(), "payment", "Top-up rejected",
                "Your top-up request was rejected. Check the request for details.");
        return PaymentRequestResponse.from(p);
    }

    private PaymentRequest findPending(Long id) {
        PaymentRequest p = paymentRequestRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Payment request not found"));
        if (p.getStatus() != PaymentStatus.PENDING) {
            throw new ApiException(HttpStatus.CONFLICT, "INVALID_STATUS_CHANGE", "Only pending requests can be reviewed");
        }
        return p;
    }
}
