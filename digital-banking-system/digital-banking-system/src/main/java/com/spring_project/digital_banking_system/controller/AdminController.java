package com.spring_project.digital_banking_system.controller;

import com.spring_project.digital_banking_system.model.Transaction;
import com.spring_project.digital_banking_system.model.User;
import com.spring_project.digital_banking_system.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final WalletService walletService;

    public AdminController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = walletService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = walletService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/bank-transfer")
    public ResponseEntity<Map<String, Object>> bankTransfer(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = walletService.bankTransfer(request);
        return ResponseEntity.ok(response);
    }
}