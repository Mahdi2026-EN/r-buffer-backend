package com.example.backend.controller;

import com.example.backend.dto.PaymentRequestCreateRequest;
import com.example.backend.dto.PaymentRequestResponse;
import com.example.backend.dto.ReviewNoteRequest;
import com.example.backend.service.PaymentRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PaymentRequestController {

    private final PaymentRequestService paymentRequestService;

    public PaymentRequestController(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

    // Customer: submit a top-up request
    @PostMapping("/api/payment-requests")
    public ResponseEntity<PaymentRequestResponse> create(Authentication auth,
                                                         @Valid @RequestBody PaymentRequestCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentRequestService.create(auth.getName(), request));
    }

    // Customer: my top-up requests
    @GetMapping("/api/payment-requests/mine")
    public List<PaymentRequestResponse> mine(Authentication auth) {
        return paymentRequestService.listMine(auth.getName());
    }

    // Admin: all top-up requests
    @GetMapping("/api/admin/payment-requests")
    public List<PaymentRequestResponse> all() {
        return paymentRequestService.listAll();
    }

    @PatchMapping("/api/admin/payment-requests/{id}/approve")
    public PaymentRequestResponse approve(@PathVariable Long id) {
        return paymentRequestService.approve(id);
    }

    @PatchMapping("/api/admin/payment-requests/{id}/reject")
    public PaymentRequestResponse reject(@PathVariable Long id, @RequestBody(required = false) ReviewNoteRequest request) {
        return paymentRequestService.reject(id, request == null ? null : request.note());
    }
}
