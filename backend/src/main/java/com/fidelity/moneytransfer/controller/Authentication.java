package com.fidelity.moneytransfer.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fidelity.moneytransfer.domain.Account;
import com.fidelity.moneytransfer.dto.AccountCreateRequest;
import com.fidelity.moneytransfer.dto.LoginRequest;
import com.fidelity.moneytransfer.dto.LoginResponse;
import com.fidelity.moneytransfer.service.AuthenticationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class Authentication {
	private final AuthenticationService authService;
	
	@Value("${jwt.expiration-ms:3600000}")
	private long tokenExpirationMs;
	
    @PostMapping("/register")
    public ResponseEntity<Account> createUser(@Valid @RequestBody AccountCreateRequest accountDto){
    	log.info("Registration request for email: {}", accountDto.email());
    	return ResponseEntity.ok(authService.createUser(accountDto));
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
        	log.info("Login request for email: {}", loginRequest.getEmail());
            Account account = authService.loginAndGetAccount(loginRequest.getEmail(), loginRequest.getPassword());
            String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setTokenType("Bearer");
            response.setEmail(loginRequest.getEmail());
            response.setMessage("Login successful");
            response.setExpiresIn(tokenExpirationMs);
            response.setAccountId(account.getId());
            
            log.info("Login successful for email: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
