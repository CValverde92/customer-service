package com.nttdata.customer_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Document(collection = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    private String id;

    @NotBlank
    private String documentNumber;

    @NotBlank
    private String name;

    @NotNull
    private CustomerType type; // ENUM: PERSONAL, BUSINESS

    private String email;
    private String phone;
    private Address address;

    private BusinessInfo businessInfo;

    private CustomerStatus status; // Enum: ACTIVE, INACTIVE, SUSPENDED

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

