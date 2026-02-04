package com.fidelity.moneytransfer.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferResponse(
        UUID transactionId,
        String status,         // e.g., "SUCCESS" or "FAILED"
        String message,        // e.g., "Transfer completed successfully"
        Long debitedFromAccountId,
        Long creditedToAccountId,
        BigDecimal amount
) {}