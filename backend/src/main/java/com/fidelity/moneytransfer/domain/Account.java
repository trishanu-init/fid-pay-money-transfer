package com.fidelity.moneytransfer.domain;

import com.fidelity.moneytransfer.exception.AccountNotActiveException;
import com.fidelity.moneytransfer.exception.InsufficientBalanceException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ACCOUNTS")
public class Account {

    @Id
    private String id;

    @Column(nullable = false)
    private String holderName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;
    
    @Column(nullable = false)
    private String password;

    @Version
    private Integer version;

    private LocalDateTime lastUpdated;

    @Column(name = "reward_points", nullable = false)
    private Integer rewardPoints = 0;

    @PreUpdate
    public void updateTimestamp() {
        this.lastUpdated = LocalDateTime.now();
    }

    public void debit(BigDecimal amount) {
        if (this.status != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException("Account is not active");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient funds");
        }
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        if (this.status != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException("Account is not active");
        }
        this.balance = this.balance.add(amount);
    }
}