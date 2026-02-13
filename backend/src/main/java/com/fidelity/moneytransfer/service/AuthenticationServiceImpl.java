package com.fidelity.moneytransfer.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.fidelity.moneytransfer.domain.Account;
import com.fidelity.moneytransfer.domain.AccountStatus;
import com.fidelity.moneytransfer.dto.AccountCreateRequest;
import com.fidelity.moneytransfer.exception.DuplicateEmailException;
import com.fidelity.moneytransfer.repository.AccountRepository;
import com.fidelity.moneytransfer.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil; // Inject JWT utility

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

        // Generate unique 10-digit ID
        long uniqueId;
        do {
            uniqueId = generateUnique10DigitId();
        } while (accountRepository.existsById(uniqueId));

        account.setId(uniqueId);
        account.setBalance(new BigDecimal(0));
        account.setLastUpdated(LocalDateTime.now());
        account.setStatus(AccountStatus.ACTIVE);

        log.info("User registered successfully with ID: {} for email: {}", uniqueId, accountDto.email());
        return accountRepository.save(account);
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

        log.info("User logged in successfully: {}", email);
        // Generate JWT token if credentials are valid
        return jwtUtil.generateToken(email);
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

        log.info("User logged in successfully with account ID: {}", account.getId());
        return account;
    }

    private long generateUnique10DigitId() {
        Random random = new Random();
        // Generate a random 10-digit number (1000000000 to 9999999999)
        return 1000000000L + (long) (random.nextDouble() * 9000000000L);
    }
}

