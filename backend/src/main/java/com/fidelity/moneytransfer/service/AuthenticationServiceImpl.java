package com.fidelity.moneytransfer.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.fidelity.moneytransfer.domain.Account;
import com.fidelity.moneytransfer.domain.AccountStatus;
import com.fidelity.moneytransfer.dto.AccountCreateRequest;
import com.fidelity.moneytransfer.repository.AccountRepository;
import com.fidelity.moneytransfer.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil; // Inject JWT utility

    @Override
    public Account createUser(AccountCreateRequest accountDto) {
        Account account = new Account();
        account.setEmail(accountDto.email());
        account.setHolderName(accountDto.username());

        String hashedPassword = BCrypt.hashpw(accountDto.password(), BCrypt.gensalt());
        account.setPassword(hashedPassword);

        account.setBalance(new BigDecimal(0));
        account.setLastUpdated(LocalDateTime.now());
        account.setStatus(AccountStatus.ACTIVE);

        return accountRepository.save(account);
    }

    @Override
    public String login(String email, String password) {
    	 Account account = accountRepository.findByEmail(email)
    	            .orElseThrow(() -> new RuntimeException("Invalid username or password"));

    	    // Validate the password using BCrypt
    	    if (!BCrypt.checkpw(password, account.getPassword())) {
    	        throw new RuntimeException("Invalid username or password");
    	    }

        // Generate JWT token if credentials are valid
        return jwtUtil.generateToken(email);
    }
}
