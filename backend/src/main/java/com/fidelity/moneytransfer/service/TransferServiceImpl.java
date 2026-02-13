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
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

        private final AccountRepository accountRepository;
        private final TransactionLogRepository transactionLogRepository;
        private final EmailService emailService;
        private final TransactionTemplate transactionTemplate;

        @Override
        public TransferResponse transferMoney(TransferRequest request) {

                if (transactionLogRepository.existsByIdempotencyKey(request.idempotencyKey())) {
                        throw new DuplicateTransferException("Transaction with this Idempotency Key already processed");
                }

                try {
                        TransactionLog successLog = transactionTemplate.execute(status -> {
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

                                return transactionLogRepository.save(log);
                        });

                        Account sender = accountRepository.findById(request.fromAccountId()).orElseThrow();
                        if (sender.getEmail() != null && !sender.getEmail().isEmpty()) {
                                emailService.sendTransactionNotification(
                                        sender.getEmail(),
                                        successLog.getId().toString(),
                                        request.amount(),
                                        "DEBIT"
                                );
                        }

                        Account receiver = accountRepository.findById(request.toAccountId()).orElseThrow();
                        if (receiver.getEmail() != null && !receiver.getEmail().isEmpty()) {
                                emailService.sendTransactionNotification(
                                        receiver.getEmail(),
                                        successLog.getId().toString(),
                                        request.amount(),
                                        "CREDIT"
                                );
                        }

                        return new TransferResponse(
                                successLog.getId(),
                                "SUCCESS",
                                "Transfer completed successfully",
                                request.fromAccountId(),
                                request.toAccountId(),
                                request.amount()
                        );

                } catch (Exception e) {
                        TransactionLog failLog = new TransactionLog();
                        failLog.setFromAccountId(request.fromAccountId());
                        failLog.setToAccountId(request.toAccountId());
                        failLog.setAmount(request.amount());
                        failLog.setStatus(TransactionStatus.FAILED);

                        String failureReason = e.getMessage() != null ? e.getMessage() : "Unknown Error";
                        if (failureReason.length() > 255) {
                                failureReason = failureReason.substring(0, 255);
                        }
                        failLog.setFailureReason(failureReason);

                        failLog.setIdempotencyKey(request.idempotencyKey());
                        failLog.setCreatedOn(LocalDateTime.now());

                        transactionLogRepository.save(failLog);

                        throw e;
                }
        }
}