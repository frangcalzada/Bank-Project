package com.bankproject.bankproject_core.customers.api.dto;

import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String email,
        String kycStatus
) {}
