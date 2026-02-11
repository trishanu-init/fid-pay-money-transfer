package com.fidelity.moneytransfer.dto;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;

    // Getters and Setters
}
