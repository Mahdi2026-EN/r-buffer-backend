package com.example.backend.controller;

import com.example.backend.dto.CreditAdjustRequest;
import com.example.backend.dto.CreditTransactionResponse;
import com.example.backend.service.CreditService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CreditController {

    private final CreditService creditService;

    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    // Customer: my credit history
    @GetMapping("/api/credit/mine")
    public List<CreditTransactionResponse> mine(Authentication auth) {
        return creditService.listMine(auth.getName());
    }

    // Admin: full credit ledger
    @GetMapping("/api/admin/credit/transactions")
    public List<CreditTransactionResponse> all() {
        return creditService.listAll();
    }

    // Admin: add or remove credit for one user
    @PostMapping("/api/admin/users/{userId}/credit")
    public ResponseEntity<Void> adjust(@PathVariable Long userId, @Valid @RequestBody CreditAdjustRequest request) {
        creditService.adjust(userId, request.type(), request.amount(), request.note());
        return ResponseEntity.noContent().build();
    }
}
