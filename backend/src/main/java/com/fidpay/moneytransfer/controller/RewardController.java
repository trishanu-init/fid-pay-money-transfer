package com.fidpay.moneytransfer.controller;

import com.fidpay.moneytransfer.domain.Account;
import com.fidpay.moneytransfer.domain.RewardDetail;
import com.fidpay.moneytransfer.exception.AccountNotFoundException;
import com.fidpay.moneytransfer.exception.UnauthorizedAccessException;
import com.fidpay.moneytransfer.repository.AccountRepository;
import com.fidpay.moneytransfer.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller for Reward operations
 */
@RestController
@RequestMapping("/api/v1/rewards")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class RewardController {

    private final AccountRepository accountRepository;
    private final RewardRepository rewardRepository;

    /**
     * Get reward points for the authenticated user
     */
    @GetMapping("/points")
    public ResponseEntity<Integer> getRewardPoints() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            log.info("Fetching reward points for user: {}", email);

            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));

            return ResponseEntity.ok(account.getRewardPoints());
        } catch (Exception e) {
            log.error("Failed to retrieve reward points", e);
            throw new UnauthorizedAccessException(e.getMessage());
        }
    }

    /**
     * Get reward history logs for the authenticated user
     */
    @GetMapping("/history")
    public ResponseEntity<List<RewardDetail>> getRewardHistory() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            log.info("Fetching reward history for user: {}", email);

            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));

            List<RewardDetail> history = rewardRepository.findByAccountIdOrderByCreatedOnDesc(account.getId());
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Failed to retrieve reward history", e);
            throw new UnauthorizedAccessException(e.getMessage());
        }
    }
}
