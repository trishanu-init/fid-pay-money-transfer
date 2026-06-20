package com.fidelity.moneytransfer.service;

import com.fidelity.moneytransfer.domain.Account;
import com.fidelity.moneytransfer.domain.AccountStatus;
import com.fidelity.moneytransfer.domain.AccountRole;
import com.fidelity.moneytransfer.domain.RewardDetail;
import com.fidelity.moneytransfer.domain.TransactionLog;
import com.fidelity.moneytransfer.dto.AccountResponse;
import com.fidelity.moneytransfer.repository.AccountRepository;
import com.fidelity.moneytransfer.repository.RewardRepository;
import com.fidelity.moneytransfer.repository.TransactionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionLogRepository transactionLogRepository;

    @Mock
    private RewardRepository rewardRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId("FIDPY100001");
        testAccount.setHolderName("John Doe");
        testAccount.setEmail("john.doe@example.com");
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setStatus(AccountStatus.ACTIVE);
        testAccount.setRole(AccountRole.USER);
        testAccount.setRewardPoints(10);
        testAccount.setLastUpdated(LocalDateTime.now());
    }

    @Test
    void testGetAllAccounts() {
        when(accountRepository.findAll()).thenReturn(Arrays.asList(testAccount));

        List<AccountResponse> result = adminService.getAllAccounts();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).holderName());
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void testUpdateAccountStatus() {
        when(accountRepository.findById("FIDPY100001")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        AccountResponse result = adminService.updateAccountStatus("FIDPY100001", AccountStatus.DISABLED);

        assertEquals(AccountStatus.DISABLED, result.status());
        verify(accountRepository, times(1)).findById("FIDPY100001");
        verify(accountRepository, times(1)).save(testAccount);
    }

    @Test
    void testGetAllTransactions() {
        TransactionLog log1 = new TransactionLog();
        log1.setFromAccountId("FIDPY100001");
        log1.setToAccountId("FIDPY100002");
        log1.setAmount(new BigDecimal("100.00"));

        when(transactionLogRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(log1));

        List<TransactionLog> result = adminService.getAllTransactions();

        assertEquals(1, result.size());
        assertEquals("FIDPY100001", result.get(0).getFromAccountId());
        verify(transactionLogRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testGetAllRewards() {
        RewardDetail reward = new RewardDetail();
        reward.setAccountId("FIDPY100001");
        reward.setPointsEarned(5);

        when(rewardRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(reward));

        List<RewardDetail> result = adminService.getAllRewards();

        assertEquals(1, result.size());
        assertEquals("FIDPY100001", result.get(0).getAccountId());
        verify(rewardRepository, times(1)).findAll(any(Sort.class));
    }
}
