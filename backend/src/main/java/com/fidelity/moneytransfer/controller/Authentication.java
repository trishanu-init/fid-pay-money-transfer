package com.fidelity.moneytransfer.controller;

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
import com.fidelity.moneytransfer.service.AuthenticationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class Authentication {
	private final AuthenticationService authService;
	
    @PostMapping("/register")
    public ResponseEntity<Account> createUser(@RequestBody AccountCreateRequest accountDto){
    	log.error("hi");
    	return ResponseEntity.ok(authService.createUser(accountDto));
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
