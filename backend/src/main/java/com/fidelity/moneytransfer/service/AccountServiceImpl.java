package com.fidelity.moneytransfer.service;

import com.fidelity.moneytransfer.domain.Account;
import com.fidelity.moneytransfer.domain.TransactionLog;
import com.fidelity.moneytransfer.domain.TransactionStatus;
import com.fidelity.moneytransfer.dto.AccountResponse;
import com.fidelity.moneytransfer.dto.TransferRequest;
import com.fidelity.moneytransfer.dto.TransferResponse;
import com.fidelity.moneytransfer.exception.AccountNotFoundException;
import com.fidelity.moneytransfer.exception.DuplicateTransferException;
import com.fidelity.moneytransfer.repository.AccountRepository;
import com.fidelity.moneytransfer.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionLogRepository transactionLogRepository;

    @Override
    @Transactional // CRITICAL: Ensures both debit and credit happen, or neither happens
    public TransferResponse transferMoney(TransferRequest request) {

        // 1. Idempotency Check (Prevent duplicate processing)
        if (transactionLogRepository.existsByIdempotencyKey(request.idempotencyKey())) {
            throw new DuplicateTransferException("Transaction with this Idempotency Key already processed");
        }

        // 2. Load Accounts
        Account fromAccount = accountRepository.findById(request.fromAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Source account not found"));

        Account toAccount = accountRepository.findById(request.toAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Destination account not found"));

        // 3. Perform Business Logic (Debit/Credit)
        // Note: The debit() method inside Account entity handles the "Insufficient Balance" check
        fromAccount.debit(request.amount());
        toAccount.credit(request.amount());

        // 4. Save Updates
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // 5. Log the Transaction
        TransactionLog log = new TransactionLog();
        log.setFromAccountId(fromAccount.getId());   // Matches your entity
        log.setToAccountId(toAccount.getId());       // Matches your entity
        log.setAmount(request.amount());
        log.setStatus(TransactionStatus.SUCCESS);
        log.setIdempotencyKey(request.idempotencyKey());
        log.setCreatedOn(LocalDateTime.now());

        transactionLogRepository.save(log);

        // 6. Return Response
        return new TransferResponse(
                log.getId(),
                "SUCCESS",
                "Transfer completed successfully",
                fromAccount.getId(),
                toAccount.getId(),
                request.amount()
        );
    }

    @Override
    public AccountResponse getAccountDetails(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        return new AccountResponse(
                account.getId(),
                account.getHolderName(),
                account.getBalance(),
                account.getStatus(),
                account.getLastUpdated()
        );
    }
}