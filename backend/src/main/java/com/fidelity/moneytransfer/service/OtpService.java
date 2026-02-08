package com.fidelity.moneytransfer.service;
import com.fidelity.moneytransfer.exception.InvalidOtpException;
import com.fidelity.moneytransfer.exception.OtpExpiredException;
import com.fidelity.moneytransfer.util.OtpGenerator;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService
{
    private final OtpStorageService otpStorage;

    public String generateOtp(String identifier, int length){
        String otp= OtpGenerator.generate(length);
        log.info("Getting OTP..");
        otpStorage.saveOtp(identifier, otp);
        log.info("Returning OTP....");
        return otp;
    }

    public boolean verifyOtp(String identifier, String userOtp){
        String storedOtp= otpStorage.getOtp(identifier);
        log.info("Verifying OTP and identifier");
        if(storedOtp==null) {
            log.warn("OTP is expired or not match..");
            throw new OtpExpiredException("OTP expired or not match");
        }

        if(!storedOtp.equals(userOtp)) {
            log.warn("OTP is doesn't match..");
            throw new InvalidOtpException("OTP doesn't match");
        }
        log.info("OTP is verified");
        otpStorage.clearOtp(identifier);
        return true;
    }
}
