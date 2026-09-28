package com.fidpay.moneytransfer.repository;

import com.fidpay.moneytransfer.domain.TransactionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, UUID> {

    // Custom query method to check if a transaction with this key already exists
    boolean existsByIdempotencyKey(String idempotencyKey);

    List<TransactionLog> findByFromAccountIdOrToAccountIdOrderByCreatedOnDesc(String fromAccountId, String toAccountId);

    // Paginated query for transaction history
    Page<TransactionLog> findByFromAccountIdOrToAccountIdOrderByCreatedOnDesc(String fromAccountId, String toAccountId,
            Pageable pageable);
}