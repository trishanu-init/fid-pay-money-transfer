package com.fidelity.moneytransfer.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Table(name = "TRANSACTION_LOGS")
public class TransactionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Long fromAccountId;

    @Column(nullable = false)
    private Long toAccountId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String failureReason;

    @Column(unique = true, nullable = false)
    private String idempotencyKey;

    @Builder.Default
    private LocalDateTime createdOn = LocalDateTime.now();
}