package com.fidelity.moneytransfer.repository;

import com.fidelity.moneytransfer.domain.Account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
	Optional<Account> findByEmail(String email);
}