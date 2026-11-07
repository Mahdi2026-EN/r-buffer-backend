package com.example.backend.repository;

import com.example.backend.entity.CreditTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, Long> {

    List<CreditTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<CreditTransaction> findAllByOrderByCreatedAtDesc();
}
