package com.fidelity.moneytransfer.service;

import com.fidelity.moneytransfer.domain.Account;
import com.fidelity.moneytransfer.domain.TransactionLog;
import com.fidelity.moneytransfer.dto.AccountResponse;
import com.fidelity.moneytransfer.exception.AccountNotFoundException;
import com.fidelity.moneytransfer.repository.AccountRepository;
import com.fidelity.moneytransfer.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public AccountResponse getAccountDetails(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        return new AccountResponse(
                account.getId(),
                account.getHolderName(),
                account.getEmail(),
                account.getBalance(),
                account.getStatus(),
                account.getLastUpdated(),
                account.getRewardPoints());
    }

    @Override
    public BigDecimal getBalance(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"))
                .getBalance();
    }

    @Override
    public List<TransactionLog> getTransactionHistory(String accountId) {
        // Fetches transactions where the user is EITHER the sender OR the receiver
        return transactionLogRepository.findByFromAccountIdOrToAccountIdOrderByCreatedOnDesc(accountId, accountId);
    }

    @Override
    public Page<TransactionLog> getTransactionHistory(String accountId, Pageable pageable) {
        return transactionLogRepository.findByFromAccountIdOrToAccountIdOrderByCreatedOnDesc(accountId, accountId,
                pageable);
    }
}