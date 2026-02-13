package com.fidelity.moneytransfer.service;

import com.fidelity.moneytransfer.domain.Account;
import com.fidelity.moneytransfer.domain.TransactionLog;
import com.fidelity.moneytransfer.dto.AccountResponse;
import com.fidelity.moneytransfer.exception.AccountNotFoundException;
import com.fidelity.moneytransfer.repository.AccountRepository;
import com.fidelity.moneytransfer.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionLogRepository transactionLogRepository;

    @Override
    public AccountResponse getAccountDetails(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        return new AccountResponse(
                account.getId(),
                account.getHolderName(),
                account.getEmail(),
                account.getBalance(),
                account.getStatus(),
                account.getLastUpdated());
    }

    @Override
    public BigDecimal getBalance(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"))
                .getBalance();
    }

    @Override
    public Page<TransactionLog> getTransactionHistory(Long accountId, int page, int size) {
        // Fetches transactions where the user is EITHER the sender OR the receiver
    	Pageable pageable = PageRequest.of(page, size);
        return transactionLogRepository.findByFromAccountIdOrToAccountIdOrderByCreatedOnDesc(accountId, accountId, pageable);
    }
}