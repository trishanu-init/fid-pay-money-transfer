package com.fidelity.moneytransfer.dto;

import jakarta.validation.constraints.NotBlank;

public record OtpRequest(
        @NotBlank(message = "Account ID is required") String accountId) {
}
