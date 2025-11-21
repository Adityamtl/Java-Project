package com.spring_project.digital_banking_system.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spring_project.digital_banking_system.model.Transaction;
import com.spring_project.digital_banking_system.model.User;
import com.spring_project.digital_banking_system.model.Wallet;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Centralized repository for all data operations using JSON file storage
 */
@Repository
public class DataRepository {

    private final ObjectMapper objectMapper;
    private static final String DATA_DIR = "data/";
    private static final String USERS_FILE = "users.json";
    private static final String WALLETS_FILE = "wallets.json";
    private static final String TRANSACTIONS_FILE = "transactions.json";

    public DataRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    // ==================== USER OPERATIONS ====================
    
    public List<User> findAllUsers() {
        return readFromFile(USERS_FILE, new TypeReference<List<User>>() {});
    }

    public Optional<User> findUserById(Long id) {
        return findAllUsers().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public Optional<User> findUserByUsername(String username) {
        return findAllUsers().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        return findUserByUsername(username).isPresent();
    }

    public User saveUser(User user) {
        List<User> users = findAllUsers();
        
        if (user.getId() == null) {
            user.setId(getNextId(users));
            users.add(user);
        } else {
            users.removeIf(u -> u.getId().equals(user.getId()));
            users.add(user);
        }
        
        writeToFile(USERS_FILE, users);
        return user;
    }

    public void deleteUser(Long id) {
        List<User> users = findAllUsers();
        users.removeIf(u -> u.getId().equals(id));
        writeToFile(USERS_FILE, users);
    }

    // ==================== WALLET OPERATIONS ====================
    
    public List<Wallet> findAllWallets() {
        return readFromFile(WALLETS_FILE, new TypeReference<List<Wallet>>() {});
    }

    public Optional<Wallet> findWalletById(Long id) {
        return findAllWallets().stream()
                .filter(wallet -> wallet.getId().equals(id))
                .findFirst();
    }

    public Optional<Wallet> findWalletByUserId(Long userId) {
        return findAllWallets().stream()
                .filter(wallet -> wallet.getUserId().equals(userId))
                .findFirst();
    }

    public Optional<Wallet> findWalletByWalletCode(String walletCode) {
        return findAllWallets().stream()
                .filter(wallet -> wallet.getWalletCode().equals(walletCode))
                .findFirst();
    }

    public Wallet saveWallet(Wallet wallet) {
        List<Wallet> wallets = findAllWallets();
        
        if (wallet.getId() == null) {
            wallet.setId(getNextId(wallets));
            wallets.add(wallet);
        } else {
            wallets.removeIf(w -> w.getId().equals(wallet.getId()));
            wallets.add(wallet);
        }
        
        writeToFile(WALLETS_FILE, wallets);
        return wallet;
    }

    public void deleteWallet(Long id) {
        List<Wallet> wallets = findAllWallets();
        wallets.removeIf(w -> w.getId().equals(id));
        writeToFile(WALLETS_FILE, wallets);
    }

    // ==================== TRANSACTION OPERATIONS ====================
    
    public List<Transaction> findAllTransactions() {
        return readFromFile(TRANSACTIONS_FILE, new TypeReference<List<Transaction>>() {});
    }

    public Optional<Transaction> findTransactionById(Long id) {
        return findAllTransactions().stream()
                .filter(transaction -> transaction.getId().equals(id))
                .findFirst();
    }

    public List<Transaction> findTransactionsByWalletId(Long walletId) {
        return findAllTransactions().stream()
                .filter(t -> (t.getSenderWalletId() != null && t.getSenderWalletId().equals(walletId)) ||
                            (t.getReceiverWalletId() != null && t.getReceiverWalletId().equals(walletId)))
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<Transaction> findAllTransactionsOrderedByTimestamp() {
        return findAllTransactions().stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public Transaction saveTransaction(Transaction transaction) {
        List<Transaction> transactions = findAllTransactions();
        
        if (transaction.getId() == null) {
            transaction.setId(getNextId(transactions));
            transactions.add(transaction);
        } else {
            transactions.removeIf(t -> t.getId().equals(transaction.getId()));
            transactions.add(transaction);
        }
        
        writeToFile(TRANSACTIONS_FILE, transactions);
        return transaction;
    }

    public void deleteTransaction(Long id) {
        List<Transaction> transactions = findAllTransactions();
        transactions.removeIf(t -> t.getId().equals(id));
        writeToFile(TRANSACTIONS_FILE, transactions);
    }

    // ==================== HELPER METHODS ====================
    
    private <T> List<T> readFromFile(String fileName, TypeReference<List<T>> typeReference) {
        try {
            File file = new File(DATA_DIR + fileName);
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, typeReference);
        } catch (IOException e) {
            System.err.println("Error reading from file: " + fileName + " - " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private <T> void writeToFile(String fileName, List<T> data) {
        try {
            File file = new File(DATA_DIR + fileName);
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + fileName + " - " + e.getMessage());
            throw new RuntimeException("Failed to write to file: " + fileName, e);
        }
    }

    private Long getNextId(List<?> items) {
        if (items.isEmpty()) {
            return 1L;
        }
        return items.size() + 1L;
    }
}
