package com.example.backend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Credit ledger: every change to a user's credit is recorded here.
@Entity
@Table(name = "credit_transactions")
public class CreditTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreditTransactionType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 300)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public CreditTransaction() {
    }

    public CreditTransaction(User user, CreditTransactionType type, BigDecimal amount, String description) {
        this.user = user;
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public CreditTransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
