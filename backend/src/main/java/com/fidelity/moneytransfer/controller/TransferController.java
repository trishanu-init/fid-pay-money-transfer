package com.fidelity.moneytransfer.controller;

import com.fidelity.moneytransfer.dto.TransferRequest;
import com.fidelity.moneytransfer.dto.TransferResponse;
import com.fidelity.moneytransfer.exception.UnauthorizedAccessException;
import com.fidelity.moneytransfer.service.TransferService;
import com.fidelity.moneytransfer.service.AccountOwnershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class TransferController {

    private final TransferService transferService;
    private final AccountOwnershipService accountOwnershipService;

    @PostMapping
    public ResponseEntity<TransferResponse> transferMoney(@Valid @RequestBody TransferRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                log.info("Admin initiating transfer from account: {} to account: {}", request.fromAccountId(), request.toAccountId());
            } else {
                // Verify the authenticated user owns the source account
                accountOwnershipService.verifyAccountOwnership(request.fromAccountId());
                log.info("User initiating transfer from account: {}", request.fromAccountId());
            }

            TransferResponse response = transferService.transferMoney(request);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            log.warn("Unauthorized transfer attempt from account: {}", request.fromAccountId());
            throw new UnauthorizedAccessException(e.getMessage());
        }
    }
}