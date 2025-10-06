package com.bankproject.bankproject_core.accounts.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record OpenAccountRequest(
        @NotNull UUID customerId,
        @NotNull String type,               // "CAJA_AHORRO" | "CUENTA_CORRIENTE"
        @Pattern(regexp = "ARS|USD") String currency
) {}