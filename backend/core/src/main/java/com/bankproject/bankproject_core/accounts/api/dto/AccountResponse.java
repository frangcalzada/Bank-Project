package com.bankproject.bankproject_core.accounts.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        UUID customerId,
        String type,
        String currency,
        BigDecimal balance,
        String status
) {}