package com.fidelity.moneytransfer.repository;

import com.fidelity.moneytransfer.domain.RewardDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for RewardDetail entity
 */
@Repository
public interface RewardRepository extends JpaRepository<RewardDetail, Long> {
    List<RewardDetail> findByAccountIdOrderByCreatedOnDesc(String accountId);
}
