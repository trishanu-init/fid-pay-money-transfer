package com.fidelity.moneytransfer.service;

import com.fidelity.moneytransfer.domain.Account;
import com.fidelity.moneytransfer.exception.AccountNotFoundException;
import com.fidelity.moneytransfer.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountOwnershipService {

    private final AccountRepository accountRepository;

    /**
     * Verify that the authenticated user owns the account with the given ID
     * @param accountId The account ID to check
     * @return The account if ownership is verified
     * @throws SecurityException if the user doesn't own the account
     */
    public Account verifyAccountOwnership(String accountId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmail = authentication.getName();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (!account.getEmail().equals(authenticatedEmail)) {
            throw new SecurityException("You do not have permission to access this account");
        }

        return account;
    }

    /**
     * Verify that the authenticated user owns the account with the given email
     * @param email The email to check
     * @return The account if ownership is verified
     * @throws SecurityException if the user doesn't own the account
     */
    public Account verifyAccountOwnershipByEmail(String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmail = authentication.getName();

        if (!email.equals(authenticatedEmail)) {
            throw new SecurityException("You do not have permission to access this account");
        }

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        return account;
    }

    /**
     * Get the authenticated user's email
     */
    public String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
