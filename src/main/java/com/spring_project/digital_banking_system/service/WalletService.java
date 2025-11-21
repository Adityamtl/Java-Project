package com.spring_project.digital_banking_system.service;

import com.spring_project.digital_banking_system.dto.DepositRequest;
import com.spring_project.digital_banking_system.dto.TransferRequest;
import com.spring_project.digital_banking_system.dto.WithdrawRequest;
import com.spring_project.digital_banking_system.exception.InsufficientBalanceException;
import com.spring_project.digital_banking_system.exception.WalletNotFoundException;
import com.spring_project.digital_banking_system.model.*;
import com.spring_project.digital_banking_system.repository.TransactionRepository;
import com.spring_project.digital_banking_system.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository,
                         TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public Map<String, Object> getBalance(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

        Map<String, Object> response = new HashMap<>();
        response.put("walletCode", wallet.getWalletCode());
        response.put("balance", wallet.getBalance());
        return response;
    }

    public Map<String, Object> deposit(Long userId, DepositRequest request) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

        // Add amount to current balance
        BigDecimal newBalance = wallet.getBalance().add(request.getAmount());
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        Transaction transaction = new Transaction(
                null,
                wallet.getId(),
                request.getAmount(),
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCESS
        );
        transactionRepository.save(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Deposit successful");
        response.put("newBalance", newBalance);
        response.put("transactionId", transaction.getId());
        return response;
    }

    public Map<String, Object> withdraw(Long userId, WithdrawRequest request) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            Transaction failedTransaction = new Transaction(
                    wallet.getId(),
                    null,
                    request.getAmount(),
                    TransactionType.WITHDRAWAL,
                    TransactionStatus.FAILED
            );
            transactionRepository.save(failedTransaction);
            throw new InsufficientBalanceException("Insufficient balance");
        }

        BigDecimal newBalance = wallet.getBalance().subtract(request.getAmount());
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        Transaction transaction = new Transaction(
                wallet.getId(),
                null,
                request.getAmount(),
                TransactionType.WITHDRAWAL,
                TransactionStatus.SUCCESS
        );
        transactionRepository.save(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Withdrawal successful");
        response.put("newBalance", newBalance);
        response.put("transactionId", transaction.getId());
        return response;
    }

    public Map<String, Object> transfer(Long userId, TransferRequest request) {
        Wallet senderWallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Sender wallet not found"));

        Wallet receiverWallet = walletRepository.findByWalletCode(request.getTargetWalletCode())
                .orElseThrow(() -> new WalletNotFoundException("Target wallet not found"));

        if (senderWallet.getId().equals(receiverWallet.getId())) {
            throw new IllegalArgumentException("Cannot transfer to your own wallet");
        }

        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            Transaction failedTransaction = new Transaction(
                    senderWallet.getId(),
                    receiverWallet.getId(),
                    request.getAmount(),
                    TransactionType.TRANSFER,
                    TransactionStatus.FAILED
            );
            transactionRepository.save(failedTransaction);
            throw new InsufficientBalanceException("Insufficient balance");
        }

        BigDecimal senderNewBalance = senderWallet.getBalance().subtract(request.getAmount());
        BigDecimal receiverNewBalance = receiverWallet.getBalance().add(request.getAmount());

        senderWallet.setBalance(senderNewBalance);
        receiverWallet.setBalance(receiverNewBalance);

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        Transaction transaction = new Transaction(
                senderWallet.getId(),
                receiverWallet.getId(),
                request.getAmount(),
                TransactionType.TRANSFER,
                TransactionStatus.SUCCESS
        );
        transactionRepository.save(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Transfer successful");
        response.put("newBalance", senderNewBalance);
        response.put("transactionId", transaction.getId());
        response.put("recipientWalletCode", receiverWallet.getWalletCode());
        return response;
    }

    public List<Transaction> getHistory(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

        return transactionRepository.findByWalletId(wallet.getId());
    }
}