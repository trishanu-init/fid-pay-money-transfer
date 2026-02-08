package com.fidelity.moneytransfer.domain;

import com.fidelity.moneytransfer.exception.AccountNotActiveException;
import com.fidelity.moneytransfer.exception.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        // Initialize a fresh active account before every test
        account = new Account();
        account.setId(Long.valueOf(1L));
        account.setHolderName("John Doe");
        account.setEmail("john.doe@example.com");
        account.setBalance(new BigDecimal("1000.00"));
        account.setStatus(AccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should successfully debit account when balance is sufficient")
    void testDebit_Success() {
        account.debit(new BigDecimal("200.00"));

        assertEquals(new BigDecimal("800.00"), account.getBalance());
    }

    @Test
    @DisplayName("Should throw exception when debit amount exceeds balance")
    void testDebit_InsufficientBalance() {
        BigDecimal largeAmount = new BigDecimal("2000.00");

        assertThrows(InsufficientBalanceException.class, () -> {
            account.debit(largeAmount);
        });
    }

    @Test
    @DisplayName("Should throw exception when debiting from inactive account")
    void testDebit_AccountNotActive() {
        account.setStatus(AccountStatus.LOCKED);

        assertThrows(AccountNotActiveException.class, () -> {
            account.debit(new BigDecimal("100.00"));
        });
    }

    @Test
    @DisplayName("Should successfully credit account")
    void testCredit_Success() {
        account.credit(new BigDecimal("500.00"));

        assertEquals(new BigDecimal("1500.00"), account.getBalance());
    }
}