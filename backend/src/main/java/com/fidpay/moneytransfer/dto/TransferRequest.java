package com.fidpay.moneytransfer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank(message = "Source account ID is required")
        String fromAccountId,

        @NotBlank(message = "Destination account ID is required")
        String toAccountId,

        @NotNull
        @DecimalMin(value = "0.01", message = "Transfer amount must be greater than 0")
        BigDecimal amount,

        @NotBlank(message = "Idempotency key is required")
        String idempotencyKey,

        String message
) {}