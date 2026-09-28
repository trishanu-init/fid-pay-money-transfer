package com.fidpay.moneytransfer.dto;

public record OtpResponse(
        boolean success,
        String message) {
}
