package com.spring_project.digital_banking_system.controller;

import com.spring_project.digital_banking_system.model.Transaction;
import com.spring_project.digital_banking_system.service.AuthService;
import com.spring_project.digital_banking_system.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final AuthService authService;

    public WalletController(WalletService walletService, AuthService authService) {
        this.walletService = walletService;
        this.authService = authService;
    }

    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance(HttpServletRequest request) {
        Long userId = authService.getCurrentUserId(request);
        Map<String, Object> response = walletService.getBalance(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<Map<String, Object>> deposit(@RequestBody Map<String, Object> depositRequest,
                                                       HttpServletRequest request) {
        Long userId = authService.getCurrentUserId(request);
        Map<String, Object> response = walletService.deposit(userId, depositRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(@RequestBody Map<String, Object> withdrawRequest,
                                                        HttpServletRequest request) {
        Long userId = authService.getCurrentUserId(request);
        Map<String, Object> response = walletService.withdraw(userId, withdrawRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transfer(@RequestBody Map<String, Object> transferRequest,
                                                        HttpServletRequest request) {
        Long userId = authService.getCurrentUserId(request);
        Map<String, Object> response = walletService.transfer(userId, transferRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> getHistory(HttpServletRequest request) {
        Long userId = authService.getCurrentUserId(request);
        List<Transaction> history = walletService.getHistory(userId);
        return ResponseEntity.ok(history);
    }
}