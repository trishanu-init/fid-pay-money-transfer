package com.fidpay.moneytransfer.service;

import com.fidpay.moneytransfer.domain.Account;
import com.fidpay.moneytransfer.dto.AccountCreateRequest;

public interface AuthenticationService {
	Account createUser(AccountCreateRequest account);
	String login(String email, String password);
	Account loginAndGetAccount(String email, String password);
	void sendForgotPasswordOtp(String email);
	String verifyForgotPasswordOtp(String email, String otp);
	void resetPassword(String email, String resetToken, String newPassword);
}
