package com.fidelity.moneytransfer.exception;

public class InvalidOtpException extends RuntimeException{
    public InvalidOtpException(String msg){
        super(msg);
    }
}