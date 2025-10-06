package com.bankproject.bankproject_core.customers.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false, unique = true)
    private String email;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "kyc_status")
    private KycStatus kycStatus;


    @Column(nullable = false, updatable = false)
    private Instant createdAt;


    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = Instant.now();
        if(kycStatus == null) kycStatus = kycStatus.PENDING;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public enum KycStatus { PENDING, VERIFIED, REJECTED }
}
