package com.spring_project.digital_banking_system.controller;

import com.spring_project.digital_banking_system.dto.BankTransferRequest;
import com.spring_project.digital_banking_system.model.Transaction;
import com.spring_project.digital_banking_system.service.AdminService;
import com.spring_project.digital_banking_system.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AuthService authService;

    public AdminController(AdminService adminService, AuthService authService) {
        this.adminService = adminService;
        this.authService = authService;
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = adminService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/bank-transfer")
    public ResponseEntity<Map<String, Object>> bankTransfer(@Valid @RequestBody BankTransferRequest request,
                                                            HttpServletRequest httpRequest) {
        Long adminUserId = authService.getCurrentUserId(httpRequest);
        Map<String, Object> response = adminService.bankTransfer(adminUserId, request);
        return ResponseEntity.ok(response);
    }
}