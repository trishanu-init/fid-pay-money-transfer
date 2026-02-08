package com.fidelity.moneytransfer.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String errorCode,    // e.g., "TRX-400" or "ACC-404"
        String message,      // e.g., "Insufficient funds"
        LocalDateTime timestamp
) {
    // Compact constructor to auto-fill timestamp
    public ErrorResponse(String errorCode, String message) {
        this(errorCode, message, LocalDateTime.now());
    }
}