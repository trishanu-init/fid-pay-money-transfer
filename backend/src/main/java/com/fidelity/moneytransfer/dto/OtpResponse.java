package com.fidelity.moneytransfer.dto;

public record OtpResponse(
        boolean success,
        String message) {
}
