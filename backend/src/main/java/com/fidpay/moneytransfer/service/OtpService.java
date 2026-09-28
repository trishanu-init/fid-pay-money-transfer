package com.fidpay.moneytransfer.service;

import com.fidpay.moneytransfer.domain.Account;
import com.fidpay.moneytransfer.dto.OtpResponse;
import com.fidpay.moneytransfer.exception.AccountNotFoundException;
import com.fidpay.moneytransfer.exception.InvalidOtpException;
import com.fidpay.moneytransfer.exception.OtpExpiredException;
import com.fidpay.moneytransfer.repository.AccountRepository;
import com.fidpay.moneytransfer.util.OtpGenerator;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {
    private final OtpStorageService otpStorage;
    private final AccountRepository accountRepository;
    private final EmailService emailService;

    public String generateOtp(String identifier, int length) {
        String otp = OtpGenerator.generate(length);
        log.info("Getting OTP..");
        otpStorage.saveOtp(identifier, otp);
        log.info("Returning OTP....");
        return otp;
    }

    /**
     * Generate OTP and send it to the account holder's email
     * 
     * @param accountId Account ID of the sender
     * @return OtpResponse with success status
     */
    public OtpResponse generateAndSendOtpEmail(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        String otp = OtpGenerator.generate(6);
        String identifier = "transfer_" + accountId;
        otpStorage.saveOtp(identifier, otp);

        log.info("Sending OTP email to {} for account {}", account.getEmail(), accountId);
        emailService.sendOtpEmail(account.getEmail(), account.getHolderName(), otp);

        return new OtpResponse(true, "OTP sent to your registered email");
    }

    /**
     * Verify OTP for transfer
     * 
     * @param accountId Account ID of the sender
     * @param userOtp   OTP entered by user
     * @return OtpResponse with verification result
     */
    public OtpResponse verifyTransferOtp(String accountId, String userOtp) {
        String identifier = "transfer_" + accountId;
        String storedOtp = otpStorage.getOtp(identifier);

        if (storedOtp == null) {
            log.warn("OTP expired or not found for account {}", accountId);
            return new OtpResponse(false, "OTP expired or not found. Please request a new OTP.");
        }

        if (!storedOtp.equals(userOtp)) {
            log.warn("Invalid OTP for account {}", accountId);
            return new OtpResponse(false, "Invalid OTP. Please try again.");
        }

        log.info("OTP verified successfully for account {}", accountId);
        otpStorage.clearOtp(identifier);
        return new OtpResponse(true, "OTP verified successfully");
    }

    public boolean verifyOtp(String identifier, String userOtp) {
        String storedOtp = otpStorage.getOtp(identifier);
        log.info("Verifying OTP and identifier");
        if (storedOtp == null) {
            log.warn("OTP is expired or not match..");
            throw new OtpExpiredException("OTP expired or not match");
        }

        if (!storedOtp.equals(userOtp)) {
            log.warn("OTP is doesn't match..");
            throw new InvalidOtpException("OTP doesn't match");
        }
        log.info("OTP is verified");
        otpStorage.clearOtp(identifier);
        return true;
    }
}
