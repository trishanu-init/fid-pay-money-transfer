package com.fidelity.moneytransfer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
@Data
public class OtpResponse {
    private String message;
    private String otp;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private String generatedAt;
}
