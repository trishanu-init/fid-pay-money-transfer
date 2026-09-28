package com.fidpay.moneytransfer.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Reward Detail entity representing a credit of reward points
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "REWARD_DETAILS")
public class RewardDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private Integer pointsEarned;

    @Column(nullable = false)
    private BigDecimal transactionAmount;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();
}
