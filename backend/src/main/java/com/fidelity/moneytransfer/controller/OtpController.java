package com.fidelity.moneytransfer.controller;

import com.fidelity.moneytransfer.dto.OtpRequest;
import com.fidelity.moneytransfer.dto.OtpResponse;
import com.fidelity.moneytransfer.dto.OtpVerifyRequest;
import com.fidelity.moneytransfer.service.OtpService;
import com.fidelity.moneytransfer.dto.VerifyOtpResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/otp")
@Slf4j
@RequiredArgsConstructor
public class OtpController {
    private final OtpService otpService;


    @PostMapping("/generate")
    public ResponseEntity<OtpResponse> generateOtp(@RequestBody OtpRequest request){
        log.info("Getting request to Generate OTP..");
        String otp= otpService.generateOtp(request.getIdentifier(), request.getLength());

        OtpResponse response = new OtpResponse();
        response.setMessage("OTP generated successfully");
        log.info("OTP is generated....");
        response.setOtp(otp);

        log.info("Setting generation date time....");
        response.setGeneratedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyOtpResponse> verify(@RequestBody OtpVerifyRequest request){
        log.info("Getting request to verify OTP");
        log.info("Getting identifier and otp...");
        boolean isValid= otpService.verifyOtp(
                request.getIdentifier(),
                request.getOtp()
        );
        VerifyOtpResponse response = new VerifyOtpResponse();
        response.setMessage(isValid? "OTP verified successfully": "Invalid OTP");

        return ResponseEntity.ok(response);
    }
}
