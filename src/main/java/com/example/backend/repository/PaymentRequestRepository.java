package com.example.backend.repository;

import com.example.backend.entity.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {

    List<PaymentRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<PaymentRequest> findAllByOrderByCreatedAtDesc();
}
