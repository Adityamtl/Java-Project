package com.spring_project.digital_banking_system.service;

import com.spring_project.digital_banking_system.model.*;
import com.spring_project.digital_banking_system.repository.DataRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WalletService {

    private final DataRepository dataRepository;

    public WalletService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public Map<String, Object> getBalance(Long userId) {
        Wallet wallet = dataRepository.findWalletByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user"));

        Map<String, Object> response = new HashMap<>();
        response.put("walletCode", wallet.getWalletCode());
        response.put("balance", wallet.getBalance());
        return response;
    }

    public Map<String, Object> deposit(Long userId, Map<String, Object> request) {
        Wallet wallet = dataRepository.findWalletByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user"));

        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        // Add amount to current balance
        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        dataRepository.saveWallet(wallet);

        Transaction transaction = new Transaction(
                null,
                wallet.getId(),
                amount,
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCESS
        );
        dataRepository.saveTransaction(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Deposit successful");
        response.put("newBalance", newBalance);
        response.put("transactionId", transaction.getId());
        return response;
    }

    public Map<String, Object> withdraw(Long userId, Map<String, Object> request) {
        Wallet wallet = dataRepository.findWalletByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user"));

        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        if (wallet.getBalance().compareTo(amount) < 0) {
            Transaction failedTransaction = new Transaction(
                    wallet.getId(),
                    null,
                    amount,
                    TransactionType.WITHDRAWAL,
                    TransactionStatus.FAILED
            );
            dataRepository.saveTransaction(failedTransaction);
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal newBalance = wallet.getBalance().subtract(amount);
        wallet.setBalance(newBalance);
        dataRepository.saveWallet(wallet);

        Transaction transaction = new Transaction(
                wallet.getId(),
                null,
                amount,
                TransactionType.WITHDRAWAL,
                TransactionStatus.SUCCESS
        );
        dataRepository.saveTransaction(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Withdrawal successful");
        response.put("newBalance", newBalance);
        response.put("transactionId", transaction.getId());
        return response;
    }

    public Map<String, Object> transfer(Long userId, Map<String, Object> request) {
        Wallet senderWallet = dataRepository.findWalletByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        String targetWalletCode = request.get("targetWalletCode").toString();
        Wallet receiverWallet = dataRepository.findWalletByWalletCode(targetWalletCode)
                .orElseThrow(() -> new RuntimeException("Target wallet not found"));

        if (senderWallet.getId().equals(receiverWallet.getId())) {
            throw new IllegalArgumentException("Cannot transfer to your own wallet");
        }

        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            Transaction failedTransaction = new Transaction(
                    senderWallet.getId(),
                    receiverWallet.getId(),
                    amount,
                    TransactionType.TRANSFER,
                    TransactionStatus.FAILED
            );
            dataRepository.saveTransaction(failedTransaction);
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal senderNewBalance = senderWallet.getBalance().subtract(amount);
        BigDecimal receiverNewBalance = receiverWallet.getBalance().add(amount);

        senderWallet.setBalance(senderNewBalance);
        receiverWallet.setBalance(receiverNewBalance);

        dataRepository.saveWallet(senderWallet);
        dataRepository.saveWallet(receiverWallet);

        Transaction transaction = new Transaction(
                senderWallet.getId(),
                receiverWallet.getId(),
                amount,
                TransactionType.TRANSFER,
                TransactionStatus.SUCCESS
        );
        dataRepository.saveTransaction(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Transfer successful");
        response.put("newBalance", senderNewBalance);
        response.put("transactionId", transaction.getId());
        response.put("recipientWalletCode", receiverWallet.getWalletCode());
        return response;
    }

    public List<Transaction> getHistory(Long userId) {
        Wallet wallet = dataRepository.findWalletByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user"));

        return dataRepository.findTransactionsByWalletId(wallet.getId());
    }

    // Admin operations (merged from AdminService)
    public List<Transaction> getAllTransactions() {
        return dataRepository.findAllTransactions();
    }

    public List<User> getAllUsers() {
        return dataRepository.findAllUsers();
    }

    public Map<String, Object> bankTransfer(Map<String, Object> request) {
        String targetWalletCode = request.get("targetWalletCode").toString();
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        Wallet targetWallet = dataRepository.findWalletByWalletCode(targetWalletCode)
                .orElseThrow(() -> new RuntimeException("Target wallet not found"));

        BigDecimal newBalance = targetWallet.getBalance().add(amount);
        targetWallet.setBalance(newBalance);
        dataRepository.saveWallet(targetWallet);

        Transaction transaction = new Transaction(
                null,
                targetWallet.getId(),
                amount,
                TransactionType.BANK_TRANSFER,
                TransactionStatus.SUCCESS
        );
        dataRepository.saveTransaction(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Bank transfer successful");
        response.put("targetWalletCode", targetWalletCode);
        response.put("amount", amount);
        response.put("newBalance", newBalance);
        response.put("transactionId", transaction.getId());
        return response;
    }
}