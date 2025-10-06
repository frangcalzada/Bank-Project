package com.bankproject.bankproject_core.customers.infra;

import com.bankproject.bankproject_core.customers.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByEmail(String email);
}

