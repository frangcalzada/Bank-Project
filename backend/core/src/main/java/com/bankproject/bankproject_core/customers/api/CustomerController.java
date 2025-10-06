package com.bankproject.bankproject_core.customers.api;

import com.bankproject.bankproject_core.customers.api.dto.CreateCustomerRequest;
import com.bankproject.bankproject_core.customers.api.dto.CustomerResponse;
import com.bankproject.bankproject_core.customers.app.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    // Alta de cliente: requiere alcance/admin (backoffice)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public CustomerResponse create(@Valid @RequestBody CreateCustomerRequest req){
        return service.create(req);
    }


    // Obtener cliente: requiere alcance de lectura de cuentas/datos
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_accounts.read')")
    public CustomerResponse get(@PathVariable UUID id) {
        return service.get(id);
    }


}
