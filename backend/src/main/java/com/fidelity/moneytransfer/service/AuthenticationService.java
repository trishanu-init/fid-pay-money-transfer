package com.fidelity.moneytransfer.service;

import com.fidelity.moneytransfer.domain.Account;
import com.fidelity.moneytransfer.dto.AccountCreateRequest;

public interface AuthenticationService {
	Account createUser(AccountCreateRequest account);
	String login(String email, String password);
}
