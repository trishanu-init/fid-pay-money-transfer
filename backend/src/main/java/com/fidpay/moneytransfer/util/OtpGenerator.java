package com.fidpay.moneytransfer.util;

import java.security.SecureRandom;

public class OtpGenerator {

    private static final String DIGITS="0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generate(int length){

        StringBuilder sb =new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(DIGITS.charAt(secureRandom.nextInt(DIGITS.length())));
        }
        return sb.toString();
    }
}
