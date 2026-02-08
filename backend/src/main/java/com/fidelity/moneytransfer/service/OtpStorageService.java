package com.fidelity.moneytransfer.service;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpStorageService
{
    private final Map<String, String> otpMap= new ConcurrentHashMap<>();
    private final Map<String, Long> expiryMap= new ConcurrentHashMap<>();

    private static final long EXPIRY_MS = 5*60*1000; // 5 min

    public void saveOtp(String identifier, String otp){
        otpMap.put(identifier, otp);
        expiryMap.put(identifier,System.currentTimeMillis()+EXPIRY_MS);
    }

    public String getOtp(String identifier){
        Long expiry= expiryMap.get(identifier);

        if(expiry==null|| System.currentTimeMillis()>expiry) return null;
        return otpMap.get(identifier);
    }

    public void clearOtp(String identifier){
        otpMap.remove(identifier);
        expiryMap.remove(identifier);
    }
}
