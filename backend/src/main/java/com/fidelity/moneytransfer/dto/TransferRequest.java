package com.fidelity.moneytransfer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransferRequest(
        @NotNull(message = "Source account ID is required")
        Long fromAccountId,

        @NotNull(message = "Destination account ID is required")
        Long toAccountId,

        @NotNull
        @DecimalMin(value = "0.01", message = "Transfer amount must be greater than 0")
        BigDecimal amount,

        @NotNull(message = "Idempotency key is required")
        String idempotencyKey
) {}