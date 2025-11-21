package com.spring_project.digital_banking_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BankTransferRequest {
    @NotBlank(message = "Target bank code is required")
    private String targetBankCode;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    // Getters and Setters
    public String getTargetBankCode() { return targetBankCode; }
    public void setTargetBankCode(String targetBankCode) { this.targetBankCode = targetBankCode; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}