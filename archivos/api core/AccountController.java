package com.bankproject.bankproject_core.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

record BalanceDTO(String accountId, String currency, String amount) {}

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    // Ejemplo: solo usuarios con scope accounts.read pueden leer su saldo
    @GetMapping("/me/balance")
    @PreAuthorize("hasAuthority('SCOPE_accounts.read')")
    public BalanceDTO myBalance() {
        // Más adelante esto vendrá de la DB; por ahora, dummy
        return new BalanceDTO("ACC-123", "ARS", "150000.00");
    }
}