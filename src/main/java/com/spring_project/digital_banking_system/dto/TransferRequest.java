package com.spring_project.digital_banking_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferRequest {
    @NotBlank(message = "Target wallet code is required")
    private String targetWalletCode;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    // Getters and Setters
    public String getTargetWalletCode() { return targetWalletCode; }
    public void setTargetWalletCode(String targetWalletCode) { this.targetWalletCode = targetWalletCode; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}