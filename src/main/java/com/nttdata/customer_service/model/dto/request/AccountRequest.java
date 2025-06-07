package com.nttdata.customer_service.model.dto.request;

import com.nttdata.customer_service.model.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * DTO para la creación de cuentas en account-service
 * Validaciones incluidas para los tipos de cuentas bancarias
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotNull(message = "Account type is required")
    private AccountType type; // CHECKING, SAVINGS, FIXED_TERM

    @PositiveOrZero(message = "Initial balance cannot be negative")
    private Double initialBalance;

}