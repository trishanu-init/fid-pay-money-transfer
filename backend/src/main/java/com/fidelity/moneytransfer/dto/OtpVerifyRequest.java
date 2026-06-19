package com.fidelity.moneytransfer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OtpVerifyRequest(
        @NotBlank(message = "Account ID is required") String accountId,

        @NotBlank(message = "OTP is required") @Size(min = 6, max = 6, message = "OTP must be 6 digits") String otp) {
}