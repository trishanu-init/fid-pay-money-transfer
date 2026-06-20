package com.fidelity.moneytransfer.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.fidelity.moneytransfer.domain.Account;
import com.fidelity.moneytransfer.domain.AccountStatus;
import com.fidelity.moneytransfer.dto.AccountCreateRequest;
import com.fidelity.moneytransfer.exception.DuplicateEmailException;
import com.fidelity.moneytransfer.exception.AccountNotFoundException;
import com.fidelity.moneytransfer.repository.AccountRepository;
import com.fidelity.moneytransfer.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil; // Inject JWT utility
    private final OtpService otpService;
    private final OtpStorageService otpStorage;
    private final EmailService emailService;

    @Override
    public Account createUser(AccountCreateRequest accountDto) {
        // Check if email already exists
        if (accountRepository.findByEmail(accountDto.email()).isPresent()) {
            log.warn("Registration attempt with existing email: {}", accountDto.email());
            throw new DuplicateEmailException("Email already exists. Please use a different email.");
        }

        Account account = new Account();
        account.setEmail(accountDto.email());
        account.setHolderName(accountDto.username());

        String hashedPassword = BCrypt.hashpw(accountDto.password(), BCrypt.gensalt());
        account.setPassword(hashedPassword);

        // Generate a unique 6-character Base36 ID prefixed with FIDPY
        String accountId;
        do {
            accountId = "FIDPY" + generateBase36Id();
        } while (accountRepository.existsById(accountId));
        account.setId(accountId);

        account.setBalance(new BigDecimal(0));
        account.setLastUpdated(LocalDateTime.now());
        account.setStatus(AccountStatus.ACTIVE);

        log.info("User registered successfully: {} with account ID: {}", accountDto.email(), accountId);
        return accountRepository.save(account);
    }

    private String generateBase36Id() {
        long min = 60466176L; // 36^5
        long max = 2176782335L; // 36^6 - 1
        long range = max - min + 1;
        java.security.SecureRandom random = new java.security.SecureRandom();
        long randomValue = min + (long) (random.nextDouble() * range);
        return Long.toString(randomValue, 36).toUpperCase();
    }

    @Override
    public String login(String email, String password) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login attempt with non-existent email: {}", email);
                    return new RuntimeException("Invalid email or password");
                });

        // Validate the password using BCrypt
        if (!BCrypt.checkpw(password, account.getPassword())) {
            log.warn("Failed login attempt for email: {}", email);
            throw new RuntimeException("Invalid email or password");
        }

        if (account.getStatus() == AccountStatus.DISABLED) {
            log.warn("Login attempt blocked: Account is disabled for email: {}", email);
            throw new RuntimeException("Account is disabled. Please contact support.");
        }

        log.info("User logged in successfully: {}", email);
        // Generate JWT token if credentials are valid
        return jwtUtil.generateToken(email, account.getRole().name());
    }

    @Override
    public Account loginAndGetAccount(String email, String password) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login attempt with non-existent email: {}", email);
                    return new RuntimeException("Invalid email or password");
                });

        // Validate the password using BCrypt
        if (!BCrypt.checkpw(password, account.getPassword())) {
            log.warn("Failed login attempt for email: {}", email);
            throw new RuntimeException("Invalid email or password");
        }

        if (account.getStatus() == AccountStatus.DISABLED) {
            log.warn("Login attempt blocked: Account is disabled for email: {}", email);
            throw new RuntimeException("Account is disabled. Please contact support.");
        }

        log.info("User logged in successfully with account ID: {}", account.getId());
        return account;
    }

    @Override
    public void sendForgotPasswordOtp(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with this email"));
        
        String otp = otpService.generateOtp("forgot_otp_" + email, 6);
        emailService.sendForgotPasswordOtpEmail(account.getEmail(), account.getHolderName(), otp);
    }

    @Override
    public String verifyForgotPasswordOtp(String email, String otp) {
        accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with this email"));
        
        boolean isVerified = otpService.verifyOtp("forgot_otp_" + email, otp);
        if (!isVerified) {
            throw new RuntimeException("Invalid OTP");
        }
        
        String resetToken = UUID.randomUUID().toString();
        otpStorage.saveOtp("forgot_token_" + email, resetToken);
        return resetToken;
    }

    @Override
    public void resetPassword(String email, String resetToken, String newPassword) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with this email"));
        
        String storedToken = otpStorage.getOtp("forgot_token_" + email);
        if (storedToken == null || !storedToken.equals(resetToken)) {
            throw new RuntimeException("Invalid or expired password reset session. Please start over.");
        }
        
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        account.setPassword(hashedPassword);
        accountRepository.save(account);
        
        otpStorage.clearOtp("forgot_token_" + email);
        log.info("Password reset successfully for email: {}", email);
    }
}
