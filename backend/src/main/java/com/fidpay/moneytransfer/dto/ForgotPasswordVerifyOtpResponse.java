package com.fidpay.moneytransfer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordVerifyOtpResponse {
    private boolean success;
    private String message;
    private String resetToken;
}
