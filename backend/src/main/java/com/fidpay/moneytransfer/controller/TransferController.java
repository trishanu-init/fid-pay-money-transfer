package com.fidpay.moneytransfer.controller;

import com.fidpay.moneytransfer.dto.TransferRequest;
import com.fidpay.moneytransfer.dto.TransferResponse;
import com.fidpay.moneytransfer.exception.UnauthorizedAccessException;
import com.fidpay.moneytransfer.service.TransferService;
import com.fidpay.moneytransfer.service.AccountOwnershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
            // Verify the authenticated user owns the source account
            accountOwnershipService.verifyAccountOwnership(request.fromAccountId());
            log.info("User initiating transfer from account: {}", request.fromAccountId());
            TransferResponse response = transferService.transferMoney(request);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            log.warn("Unauthorized transfer attempt from account: {}", request.fromAccountId());
            throw new UnauthorizedAccessException(e.getMessage());
        }
    }
}