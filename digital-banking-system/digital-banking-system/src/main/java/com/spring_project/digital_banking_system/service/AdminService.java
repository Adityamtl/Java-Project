package com.spring_project.digital_banking_system.service;

import com.spring_project.digital_banking_system.dto.BankTransferRequest;
import com.spring_project.digital_banking_system.exception.InsufficientBalanceException;
import com.spring_project.digital_banking_system.exception.WalletNotFoundException;
import com.spring_project.digital_banking_system.model.*;
import com.spring_project.digital_banking_system.repository.TransactionRepository;
import com.spring_project.digital_banking_system.repository.UserRepository;
import com.spring_project.digital_banking_system.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public AdminService(TransactionRepository transactionRepository,
                        UserRepository userRepository,
                        WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllByOrderByTimestampDesc();
    }

    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("email", user.getEmail());
            userInfo.put("role", user.getRole());

            walletRepository.findByUserId(user.getId()).ifPresent(wallet -> {
                userInfo.put("walletCode", wallet.getWalletCode());
                userInfo.put("balance", wallet.getBalance());
            });

            return userInfo;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> bankTransfer(Long adminUserId, BankTransferRequest request) {
        Wallet adminWallet = walletRepository.findByUserId(adminUserId)
                .orElseThrow(() -> new WalletNotFoundException("Admin wallet not found"));

        Wallet targetWallet = walletRepository.findByWalletCode(request.getTargetBankCode())
                .orElseThrow(() -> new WalletNotFoundException("Target bank wallet not found"));

        if (adminWallet.getBalance().compareTo(request.getAmount()) < 0) {
            Transaction failedTransaction = new Transaction(
                    adminWallet.getId(),
                    targetWallet.getId(),
                    request.getAmount(),
                    TransactionType.BANK_TRANSFER,
                    TransactionStatus.FAILED
            );
            transactionRepository.save(failedTransaction);
            throw new InsufficientBalanceException("Insufficient balance for bank transfer");
        }

        BigDecimal adminNewBalance = adminWallet.getBalance().subtract(request.getAmount());
        BigDecimal targetNewBalance = targetWallet.getBalance().add(request.getAmount());

        adminWallet.setBalance(adminNewBalance);
        targetWallet.setBalance(targetNewBalance);

        walletRepository.save(adminWallet);
        walletRepository.save(targetWallet);

        Transaction transaction = new Transaction(
                adminWallet.getId(),
                targetWallet.getId(),
                request.getAmount(),
                TransactionType.BANK_TRANSFER,
                TransactionStatus.SUCCESS
        );
        transactionRepository.save(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Bank transfer successful");
        response.put("newBalance", adminNewBalance);
        response.put("transactionId", transaction.getId());
        response.put("targetBankCode", targetWallet.getWalletCode());
        return response;
    }
}