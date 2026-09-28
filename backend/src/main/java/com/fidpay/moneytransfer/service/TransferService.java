package com.fidpay.moneytransfer.service;

import com.fidpay.moneytransfer.dto.TransferRequest;
import com.fidpay.moneytransfer.dto.TransferResponse;

public interface TransferService {
    TransferResponse transferMoney(TransferRequest request);
}