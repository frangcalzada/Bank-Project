package com.bankproject.bankproject_core.accounts.infra;

import com.bankproject.bankproject_core.accounts.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByCustomerId(UUID customerId);
}