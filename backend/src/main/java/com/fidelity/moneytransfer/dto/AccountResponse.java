package com.fidelity.moneytransfer.dto;

import com.fidelity.moneytransfer.domain.AccountStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long accountId,
        String holderName,
        BigDecimal balance,
        AccountStatus status,
        LocalDateTime lastUpdated
) {}