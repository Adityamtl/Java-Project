package com.spring_project.digital_banking_system.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.spring_project.digital_banking_system.model.User;
import com.spring_project.digital_banking_system.service.FileStorageService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User data using file-based storage
 */
@Repository
public class UserRepository {
    
    private final FileStorageService fileStorageService;
    private static final String FILE_NAME = "users.json";

    public UserRepository(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public List<User> findAll() {
        return fileStorageService.readFromFile(FILE_NAME, new TypeReference<List<User>>() {});
    }

    public Optional<User> findById(Long id) {
        List<User> users = findAll();
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public Optional<User> findByUsername(String username) {
        List<User> users = findAll();
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public User save(User user) {
        List<User> users = findAll();
        
        if (user.getId() == null) {
            // New user - assign ID
            Long newId = fileStorageService.getNextId(users);
            user.setId(newId);
            users.add(user);
        } else {
            // Update existing user
            users.removeIf(u -> u.getId().equals(user.getId()));
            users.add(user);
        }
        
        fileStorageService.writeToFile(FILE_NAME, users);
        return user;
    }

    public void delete(User user) {
        List<User> users = findAll();
        users.removeIf(u -> u.getId().equals(user.getId()));
        fileStorageService.writeToFile(FILE_NAME, users);
    }

    public void deleteById(Long id) {
        List<User> users = findAll();
        users.removeIf(u -> u.getId().equals(id));
        fileStorageService.writeToFile(FILE_NAME, users);
    }
}
