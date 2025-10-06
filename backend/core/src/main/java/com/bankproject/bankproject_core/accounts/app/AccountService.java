package com.bankproject.bankproject_core.accounts.app;

import com.bankproject.bankproject_core.accounts.api.dto.AccountResponse;
import com.bankproject.bankproject_core.accounts.api.dto.OpenAccountRequest;
import com.bankproject.bankproject_core.accounts.domain.Account;
import com.bankproject.bankproject_core.accounts.domain.Account.AccountStatus;
import com.bankproject.bankproject_core.accounts.domain.Account.AccountType;
import com.bankproject.bankproject_core.accounts.infra.AccountRepository;
import com.bankproject.bankproject_core.customers.infra.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accounts;
  private final CustomerRepository customers;

  public AccountResponse open(OpenAccountRequest req) {
    var customer = customers.findById(req.customerId())
        .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

    var acc = Account.builder()
        .customer(customer)
        .type(AccountType.valueOf(req.type()))
        .currency(req.currency())
        .status(AccountStatus.ACTIVE)
        .build();

    acc = accounts.save(acc);
    return toResponse(acc);
  }

  public AccountResponse get(UUID id) {
    var acc = accounts.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrada"));
    return toResponse(acc);
  }

  private static AccountResponse toResponse(Account a) {
    return new AccountResponse(
        a.getId(),
        a.getCustomer().getId(),
        a.getType().name(),
        a.getCurrency(),
        a.getBalance(),
        a.getStatus().name()
    );
  }
}