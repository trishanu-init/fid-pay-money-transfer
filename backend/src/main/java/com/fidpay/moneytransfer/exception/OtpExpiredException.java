package com.fidpay.moneytransfer.exception;

public class OtpExpiredException extends RuntimeException{
    public OtpExpiredException(String msg){
        super(msg);
    }
}