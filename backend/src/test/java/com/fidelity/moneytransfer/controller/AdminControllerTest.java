package com.fidelity.moneytransfer.controller;

import com.fidelity.moneytransfer.domain.AccountStatus;
import com.fidelity.moneytransfer.domain.RewardDetail;
import com.fidelity.moneytransfer.domain.TransactionLog;
import com.fidelity.moneytransfer.dto.AccountResponse;
import com.fidelity.moneytransfer.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private AccountResponse testAccountResponse;

    @BeforeEach
    void setUp() {
        testAccountResponse = new AccountResponse(
                "FIDPY100001", "John Doe", "john.doe@example.com",
                new BigDecimal("1000.00"), AccountStatus.ACTIVE, LocalDateTime.now(), 10
        );
    }

    @Test
    void testGetAllAccounts() {
        when(adminService.getAllAccounts()).thenReturn(Arrays.asList(testAccountResponse));

        ResponseEntity<List<AccountResponse>> response = adminController.getAllAccounts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).holderName());
        verify(adminService, times(1)).getAllAccounts();
    }

    @Test
    void testUpdateAccountStatus() {
        when(adminService.updateAccountStatus("FIDPY100001", AccountStatus.DISABLED))
                .thenReturn(testAccountResponse);

        ResponseEntity<AccountResponse> response = adminController.updateAccountStatus("FIDPY100001", AccountStatus.DISABLED);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("FIDPY100001", response.getBody().accountId());
        verify(adminService, times(1)).updateAccountStatus("FIDPY100001", AccountStatus.DISABLED);
    }

    @Test
    void testGetAllTransactions() {
        TransactionLog log = new TransactionLog();
        log.setFromAccountId("FIDPY100001");
        log.setToAccountId("FIDPY100002");
        log.setAmount(new BigDecimal("100.00"));

        when(adminService.getAllTransactions()).thenReturn(Arrays.asList(log));

        ResponseEntity<List<TransactionLog>> response = adminController.getAllTransactions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("FIDPY100001", response.getBody().get(0).getFromAccountId());
        verify(adminService, times(1)).getAllTransactions();
    }

    @Test
    void testGetAllRewards() {
        RewardDetail reward = new RewardDetail();
        reward.setAccountId("FIDPY100001");
        reward.setPointsEarned(5);

        when(adminService.getAllRewards()).thenReturn(Arrays.asList(reward));

        ResponseEntity<List<RewardDetail>> response = adminController.getAllRewards();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("FIDPY100001", response.getBody().get(0).getAccountId());
        verify(adminService, times(1)).getAllRewards();
    }
}
