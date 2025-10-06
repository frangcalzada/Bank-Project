
package com.bankproject.bankproject_core.customers.app;

import com.bankproject.bankproject_core.customers.api.dto.CreateCustomerRequest;
import com.bankproject.bankproject_core.customers.api.dto.CustomerResponse;
import com.bankproject.bankproject_core.customers.domain.Customer;
import com.bankproject.bankproject_core.customers.domain.Customer.KycStatus;
import com.bankproject.bankproject_core.customers.infra.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repo;

    public CustomerResponse create(CreateCustomerRequest req) {
        if (repo.existsByEmail(req.email()))
            throw new IllegalArgumentException("Email ya registrado");

        var c = Customer.builder()
                .name(req.name())
                .email(req.email())
                .kycStatus(KycStatus.PENDING)
                .build();

        c = repo.save(c);
        return toResponse(c);
    }

    public CustomerResponse get(UUID id) {
        var c = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        return toResponse(c);
    }

    private static CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(
                c.getId(), c.getName(), c.getEmail(), c.getKycStatus().name()
        );
    }
}
