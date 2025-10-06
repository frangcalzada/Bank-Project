package com.bankproject.bankproject_core.accounts.api;

import com.bankproject.bankproject_core.accounts.api.dto.AccountResponse;
import com.bankproject.bankproject_core.accounts.api.dto.OpenAccountRequest;
import com.bankproject.bankproject_core.accounts.app.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    // Apertura de cuenta: backoffice/admin
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public AccountResponse open(@Valid @RequestBody OpenAccountRequest req) {
        return service.open(req);
    }

    // Detalle de cuenta: requiere alcance de lectura
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_accounts.read')")
    public AccountResponse get(@PathVariable UUID id) {
        return service.get(id);
    }
}