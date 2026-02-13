package com.fidelity.moneytransfer.controller;

import com.fidelity.moneytransfer.domain.TransactionLog;
import com.fidelity.moneytransfer.dto.AccountResponse;
import com.fidelity.moneytransfer.exception.UnauthorizedAccessException;
import com.fidelity.moneytransfer.service.AccountService;
import com.fidelity.moneytransfer.service.AccountOwnershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final AccountOwnershipService accountOwnershipService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountDetails(@PathVariable Long id) {
        try {
            // Verify the authenticated user owns this account
            accountOwnershipService.verifyAccountOwnership(id);
            log.info("User accessing account: {}", id);
            return ResponseEntity.ok(accountService.getAccountDetails(id));
        } catch (SecurityException e) {
            log.warn("Unauthorized access attempt to account: {}", id);
            throw new UnauthorizedAccessException(e.getMessage());
        }
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getAccountBalance(@PathVariable Long id) {
        try {
            // Verify the authenticated user owns this account
            accountOwnershipService.verifyAccountOwnership(id);
            log.info("User checking balance for account: {}", id);
            return ResponseEntity.ok(accountService.getBalance(id));
        } catch (SecurityException e) {
            log.warn("Unauthorized access attempt to account balance: {}", id);
            throw new UnauthorizedAccessException(e.getMessage());
        }
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<Page<TransactionLog>> getAccountTransactions(@PathVariable Long id,
    		@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // Verify the authenticated user owns this account
            accountOwnershipService.verifyAccountOwnership(id);
            log.info("User accessing transactions for account: {}", id);
            return ResponseEntity.ok(accountService.getTransactionHistory(id, page, size));
        } catch (SecurityException e) {
            log.warn("Unauthorized access attempt to account transactions: {}", id);
            throw new UnauthorizedAccessException(e.getMessage());
        }
    }
}