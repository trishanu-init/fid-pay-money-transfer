package com.fidelity.moneytransfer.service;

import com.fidelity.moneytransfer.domain.Account;
import com.fidelity.moneytransfer.domain.TransactionLog;
import com.fidelity.moneytransfer.domain.TransactionStatus;
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
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

        private final AccountRepository accountRepository;
        private final TransactionLogRepository transactionLogRepository;
        private final EmailService emailService;

        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

        @Override
        @Transactional
        public TransferResponse transferMoney(TransferRequest request) {
                if (transactionLogRepository.existsByIdempotencyKey(request.idempotencyKey())) {
                        throw new DuplicateTransferException("Transaction with this Idempotency Key already processed");
                }

                Account fromAccount = accountRepository.findById(request.fromAccountId())
                                .orElseThrow(() -> new AccountNotFoundException("Source account not found"));

                Account toAccount = accountRepository.findById(request.toAccountId())
                                .orElseThrow(() -> new AccountNotFoundException("Destination account not found"));

                fromAccount.debit(request.amount());
                toAccount.credit(request.amount());

                accountRepository.save(fromAccount);
                accountRepository.save(toAccount);

                TransactionLog log = new TransactionLog();
                log.setFromAccountId(fromAccount.getId());
                log.setToAccountId(toAccount.getId());
                log.setAmount(request.amount());
                log.setStatus(TransactionStatus.SUCCESS);
                log.setIdempotencyKey(request.idempotencyKey());
                log.setCreatedOn(LocalDateTime.now());

                transactionLogRepository.save(log);

                String transactionDate = log.getCreatedOn().format(DATE_FORMATTER);

                // Notify sender (money debited)
                emailService.sendTransactionNotification(
                                fromAccount.getEmail(),
                                fromAccount.getHolderName(),
                                "DEBIT",
                                request.amount(),
                                fromAccount.getId().toString(),
                                fromAccount.getBalance(),
                                transactionDate,
                                toAccount.getHolderName(),
                                toAccount.getId().toString(),
                                log.getId().toString());

                // Notify receiver (money credited)
                emailService.sendTransactionNotification(
                                toAccount.getEmail(),
                                toAccount.getHolderName(),
                                "CREDIT",
                                request.amount(),
                                toAccount.getId().toString(),
                                toAccount.getBalance(),
                                transactionDate,
                                fromAccount.getHolderName(),
                                fromAccount.getId().toString(),
                                log.getId().toString());

                return new TransferResponse(
                                log.getId(),
                                "SUCCESS",
                                "Transfer completed successfully",
                                fromAccount.getId(),
                                toAccount.getId(),
                                request.amount());
        }
}