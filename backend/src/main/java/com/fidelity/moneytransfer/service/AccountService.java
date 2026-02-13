package com.fidelity.moneytransfer.service;

import com.fidelity.moneytransfer.domain.TransactionLog;
import com.fidelity.moneytransfer.dto.AccountResponse;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;

public interface AccountService {
    AccountResponse getAccountDetails(Long accountId);
    BigDecimal getBalance(Long accountId);
    Page<TransactionLog> getTransactionHistory(Long accountId, int page, int size);
}