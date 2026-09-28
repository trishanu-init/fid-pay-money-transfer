package com.fidpay.moneytransfer.dto;

import com.fidpay.moneytransfer.domain.AccountStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
                String accountId,
                String holderName,
                String email,
                BigDecimal balance,
                AccountStatus status,
                LocalDateTime lastUpdated,
                Integer rewardPoints) {
}