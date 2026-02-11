package com.fidelity.moneytransfer.dto;

public record AccountCreateRequest(String username,
		String password,
		String email) {
}
