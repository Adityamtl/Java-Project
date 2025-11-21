package com.spring_project.digital_banking_system.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling file-based storage using JSON files
 * This replaces database operations with file I/O
 */
@Service
public class FileStorageService {

    private final ObjectMapper objectMapper;
    private final String DATA_DIR = "data/";

    public FileStorageService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Create data directory if it doesn't exist
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    /**
     * Read data from JSON file
     */
    public <T> List<T> readFromFile(String fileName, TypeReference<List<T>> typeReference) {
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

    /**
     * Write data to JSON file
     */
    public <T> void writeToFile(String fileName, List<T> data) {
        try {
            File file = new File(DATA_DIR + fileName);
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + fileName + " - " + e.getMessage());
            throw new RuntimeException("Failed to write to file: " + fileName, e);
        }
    }

    /**
     * Get next available ID for a list of objects
     */
    public Long getNextId(List<?> items) {
        if (items.isEmpty()) {
            return 1L;
        }
        // Assuming items have getId() method
        return items.size() + 1L;
    }
}
