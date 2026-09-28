package com.fidpay.moneytransfer.repository;

import com.fidpay.moneytransfer.domain.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

    /**
     * Find the latest unverified OTP for an account
     */
    @Query("SELECT o FROM Otp o WHERE o.accountId = :accountId AND o.verified = false ORDER BY o.createdAt DESC LIMIT 1")
    Optional<Otp> findLatestUnverifiedByAccountId(@Param("accountId") String accountId);

    /**
     * Delete all expired OTPs for cleanup
     */
    @Query("DELETE FROM Otp o WHERE o.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredOtps();
}
