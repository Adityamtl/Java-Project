package com.spring_project.digital_banking_system.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.spring_project.digital_banking_system.model.Wallet;
import com.spring_project.digital_banking_system.service.FileStorageService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Wallet data using file-based storage
 */
@Repository
public class WalletRepository {
    
    private final FileStorageService fileStorageService;
    private static final String FILE_NAME = "wallets.json";

    public WalletRepository(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public List<Wallet> findAll() {
        return fileStorageService.readFromFile(FILE_NAME, new TypeReference<List<Wallet>>() {});
    }

    public Optional<Wallet> findById(Long id) {
        List<Wallet> wallets = findAll();
        return wallets.stream()
                .filter(wallet -> wallet.getId().equals(id))
                .findFirst();
    }

    public Optional<Wallet> findByUserId(Long userId) {
        List<Wallet> wallets = findAll();
        return wallets.stream()
                .filter(wallet -> wallet.getUserId().equals(userId))
                .findFirst();
    }

    public Optional<Wallet> findByWalletCode(String walletCode) {
        List<Wallet> wallets = findAll();
        return wallets.stream()
                .filter(wallet -> wallet.getWalletCode().equals(walletCode))
                .findFirst();
    }

    public Wallet save(Wallet wallet) {
        List<Wallet> wallets = findAll();
        
        if (wallet.getId() == null) {
            // New wallet - assign ID
            Long newId = fileStorageService.getNextId(wallets);
            wallet.setId(newId);
            wallets.add(wallet);
        } else {
            // Update existing wallet
            wallets.removeIf(w -> w.getId().equals(wallet.getId()));
            wallets.add(wallet);
        }
        
        fileStorageService.writeToFile(FILE_NAME, wallets);
        return wallet;
    }

    public void delete(Wallet wallet) {
        List<Wallet> wallets = findAll();
        wallets.removeIf(w -> w.getId().equals(wallet.getId()));
        fileStorageService.writeToFile(FILE_NAME, wallets);
    }

    public void deleteById(Long id) {
        List<Wallet> wallets = findAll();
        wallets.removeIf(w -> w.getId().equals(id));
        fileStorageService.writeToFile(FILE_NAME, wallets);
    }
}
