package com.spring_project.digital_banking_system.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.spring_project.digital_banking_system.model.Transaction;
import com.spring_project.digital_banking_system.service.FileStorageService;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for Transaction data using file-based storage
 */
@Repository
public class TransactionRepository {
    
    private final FileStorageService fileStorageService;
    private static final String FILE_NAME = "transactions.json";

    public TransactionRepository(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public List<Transaction> findAll() {
        return fileStorageService.readFromFile(FILE_NAME, new TypeReference<List<Transaction>>() {});
    }

    public Optional<Transaction> findById(Long id) {
        List<Transaction> transactions = findAll();
        return transactions.stream()
                .filter(transaction -> transaction.getId().equals(id))
                .findFirst();
    }

    public List<Transaction> findByWalletId(Long walletId) {
        List<Transaction> transactions = findAll();
        return transactions.stream()
                .filter(t -> (t.getSenderWalletId() != null && t.getSenderWalletId().equals(walletId)) ||
                            (t.getReceiverWalletId() != null && t.getReceiverWalletId().equals(walletId)))
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<Transaction> findAllByOrderByTimestampDesc() {
        List<Transaction> transactions = findAll();
        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public Transaction save(Transaction transaction) {
        List<Transaction> transactions = findAll();
        
        if (transaction.getId() == null) {
            // New transaction - assign ID
            Long newId = fileStorageService.getNextId(transactions);
            transaction.setId(newId);
            transactions.add(transaction);
        } else {
            // Update existing transaction
            transactions.removeIf(t -> t.getId().equals(transaction.getId()));
            transactions.add(transaction);
        }
        
        fileStorageService.writeToFile(FILE_NAME, transactions);
        return transaction;
    }

    public void delete(Transaction transaction) {
        List<Transaction> transactions = findAll();
        transactions.removeIf(t -> t.getId().equals(transaction.getId()));
        fileStorageService.writeToFile(FILE_NAME, transactions);
    }

    public void deleteById(Long id) {
        List<Transaction> transactions = findAll();
        transactions.removeIf(t -> t.getId().equals(id));
        fileStorageService.writeToFile(FILE_NAME, transactions);
    }
}
