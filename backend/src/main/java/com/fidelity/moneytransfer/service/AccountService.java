package com.fidelity.moneytransfer.service;

import com.fidelity.moneytransfer.dto.AccountResponse;
import com.fidelity.moneytransfer.dto.TransferRequest;
import com.fidelity.moneytransfer.dto.TransferResponse;

public interface AccountService {
    TransferResponse transferMoney(TransferRequest request);
    AccountResponse getAccountDetails(Long accountId);
}