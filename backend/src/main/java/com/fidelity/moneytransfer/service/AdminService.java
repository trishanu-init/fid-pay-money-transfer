package com.fidelity.moneytransfer.service;

import com.fidelity.moneytransfer.domain.AccountStatus;
import com.fidelity.moneytransfer.domain.RewardDetail;
import com.fidelity.moneytransfer.domain.TransactionLog;
import com.fidelity.moneytransfer.dto.AccountResponse;
import java.util.List;

/**
 * Service interface for Admin operations
 */
public interface AdminService {
    List<AccountResponse> getAllAccounts();
    AccountResponse updateAccountStatus(String accountId, AccountStatus status);
    List<TransactionLog> getAllTransactions();
    List<RewardDetail> getAllRewards();
}
