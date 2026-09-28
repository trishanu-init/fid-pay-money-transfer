package com.fidpay.moneytransfer.controller;

import com.fidpay.moneytransfer.dto.OtpRequest;
import com.fidpay.moneytransfer.dto.OtpResponse;
import com.fidpay.moneytransfer.dto.OtpVerifyRequest;
import com.fidpay.moneytransfer.service.OtpService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/otp")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RequiredArgsConstructor
public class OtpController {
    private final OtpService otpService;

    /**
     * Send OTP to sender's email for transfer verification
     */
    @PostMapping("/send-transfer")
    public ResponseEntity<OtpResponse> sendTransferOtp(@Valid @RequestBody OtpRequest request) {
        log.info("Sending transfer OTP for account {}", request.accountId());
        OtpResponse response = otpService.generateAndSendOtpEmail(request.accountId());
        return ResponseEntity.ok(response);
    }

    /**
     * Verify OTP for transfer
     */
    @PostMapping("/verify-transfer")
    public ResponseEntity<OtpResponse> verifyTransferOtp(@Valid @RequestBody OtpVerifyRequest request) {
        log.info("Verifying transfer OTP for account {}", request.accountId());
        OtpResponse response = otpService.verifyTransferOtp(request.accountId(), request.otp());
        return ResponseEntity.ok(response);
    }
}
