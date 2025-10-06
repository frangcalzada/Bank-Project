package com.bankproject.bankproject_core.customers.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest (
    @NotBlank String name,
    @Email @NotBlank String email
) {}
