package com.fidpay.moneytransfer.exception;

public class AccountNotActiveException extends RuntimeException {
    public AccountNotActiveException(String message) {
        super(message);
    }
}