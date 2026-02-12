package com.fidelity.moneytransfer.dto;

import jakarta.validation.constraints.NotNull;

public record OtpRequest(
        @NotNull(message = "Account ID is required") Long accountId) {
}
