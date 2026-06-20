package com.fidelity.moneytransfer.controller;

import com.fidelity.moneytransfer.domain.AccountStatus;
import com.fidelity.moneytransfer.domain.RewardDetail;
import com.fidelity.moneytransfer.domain.TransactionLog;
import com.fidelity.moneytransfer.dto.AccountResponse;
import com.fidelity.moneytransfer.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        log.info("Admin fetching all accounts");
        return ResponseEntity.ok(adminService.getAllAccounts());
    }

    @PutMapping("/accounts/{id}/status")
    public ResponseEntity<AccountResponse> updateAccountStatus(
            @PathVariable String id,
            @RequestBody AccountStatus status) {
        log.info("Admin updating status of account {} to {}", id, status);
        return ResponseEntity.ok(adminService.updateAccountStatus(id, status));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionLog>> getAllTransactions() {
        log.info("Admin fetching all transactions");
        return ResponseEntity.ok(adminService.getAllTransactions());
    }

    @GetMapping("/rewards")
    public ResponseEntity<List<RewardDetail>> getAllRewards() {
        log.info("Admin fetching all rewards");
        return ResponseEntity.ok(adminService.getAllRewards());
    }
}
