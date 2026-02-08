package com.fidelity.moneytransfer.controller;

import com.fidelity.moneytransfer.exception.InvalidOtpException;
import com.fidelity.moneytransfer.exception.OtpExpiredException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OtpExceptionHandler {
    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<String> handleInvalidOtp(InvalidOtpException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<String> handleExpiredOtp(OtpExpiredException e) {
        return ResponseEntity.status(HttpStatus.GONE).body(e.getMessage());
    }
}
