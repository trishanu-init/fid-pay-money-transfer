package com.fidelity.moneytransfer.service;

import com.fidelity.moneytransfer.domain.TransactionLog;
import com.fidelity.moneytransfer.dto.AccountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    AccountResponse getAccountDetails(Long accountId);

    BigDecimal getBalance(Long accountId);

    List<TransactionLog> getTransactionHistory(Long accountId);

    Page<TransactionLog> getTransactionHistory(Long accountId, Pageable pageable);
}