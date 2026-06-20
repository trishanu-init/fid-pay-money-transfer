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
import com.fidelity.moneytransfer.repository.RewardRepository;
import com.fidelity.moneytransfer.domain.RewardDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {

        private final AccountRepository accountRepository;
        private final TransactionLogRepository transactionLogRepository;
        private final RewardRepository rewardRepository;
        private final EmailService emailService;
        private final TransactionTemplate transactionTemplate;

        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

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

                                TransactionLog savedLog = transactionLogRepository.save(log);

                                // Check reward eligibility
                                // 1. Transaction status is SUCCESS (implicit in this block)
                                // 2. Transaction amount is greater than 100
                                // 3. Sender and receiver are different users (not self-transfer)
                                if (request.amount().compareTo(new java.math.BigDecimal("100")) > 0
                                        && !fromAccount.getId().equalsIgnoreCase(toAccount.getId())) {
                                        
                                        int points = request.amount().divide(new java.math.BigDecimal("100"), 0, java.math.RoundingMode.DOWN).intValue();
                                        if (points > 0) {
                                                fromAccount.setRewardPoints(fromAccount.getRewardPoints() + points);
                                                accountRepository.save(fromAccount);

                                                RewardDetail rewardDetail = RewardDetail.builder()
                                                        .accountId(fromAccount.getId())
                                                        .transactionId(savedLog.getId().toString())
                                                        .pointsEarned(points)
                                                        .transactionAmount(request.amount())
                                                        .createdOn(LocalDateTime.now())
                                                        .build();
                                                rewardRepository.save(rewardDetail);
                                        }
                                }

                                return savedLog;
                        });

                        Account sender = accountRepository.findById(request.fromAccountId()).orElseThrow();
                        Account receiver = accountRepository.findById(request.toAccountId()).orElseThrow();

                        String transactionDate = successLog.getCreatedOn().format(DATE_FORMATTER);

                        try {
                                emailService.sendTransactionNotification(
                                        sender.getEmail(),
                                        sender.getHolderName(),
                                        "DEBIT",
                                        request.amount(),
                                        sender.getId().toString(),
                                        sender.getBalance(),
                                        transactionDate,
                                        receiver.getHolderName(),
                                        receiver.getId().toString(),
                                        successLog.getId().toString()
                                );
                        } catch (Exception e) {
                                log.warn("Failed to send debit email", e);
                        }

                        try {
                                emailService.sendTransactionNotification(
                                        receiver.getEmail(),
                                        receiver.getHolderName(),
                                        "CREDIT",
                                        request.amount(),
                                        receiver.getId().toString(),
                                        receiver.getBalance(),
                                        transactionDate,
                                        sender.getHolderName(),
                                        sender.getId().toString(),
                                        successLog.getId().toString()
                                );
                        } catch (Exception e) {
                                log.warn("Failed to send credit email", e);
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
                        try {
                                TransactionLog failLog = new TransactionLog();
                                failLog.setFromAccountId(request.fromAccountId());
                                failLog.setToAccountId(request.toAccountId());
                                failLog.setAmount(request.amount());
                                failLog.setStatus(TransactionStatus.FAILED);
                                failLog.setIdempotencyKey(request.idempotencyKey());
                                failLog.setCreatedOn(LocalDateTime.now());

                                String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown Error";
                                if (errorMsg.length() > 255) {
                                        errorMsg = errorMsg.substring(0, 255);
                                }
                                failLog.setFailureReason(errorMsg);

                                transactionLogRepository.save(failLog);
                        } catch (Exception logEx) {
                                log.error("Failed to save error log", logEx);
                        }

                        throw e;
                }
        }
}