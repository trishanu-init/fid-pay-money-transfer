package com.fidelity.moneytransfer.repository;

import com.fidelity.moneytransfer.domain.TransactionLog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, UUID> {

    // Custom query method to check if a transaction with this key already exists
    boolean existsByIdempotencyKey(String idempotencyKey);
    Page<TransactionLog> findByFromAccountIdOrToAccountIdOrderByCreatedOnDesc(Long fromAccountId, Long toAccountId, Pageable pageable);
}