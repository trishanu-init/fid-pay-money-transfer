package com.fidelity.moneytransfer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private String message;
    private String email;
    private Long expiresIn; // Token expiration in milliseconds
    private String accountId;
    private String role;
}
