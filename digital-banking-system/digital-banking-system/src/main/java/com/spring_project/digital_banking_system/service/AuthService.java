package com.spring_project.digital_banking_system.service;

import com.spring_project.digital_banking_system.model.Role;
import com.spring_project.digital_banking_system.model.User;
import com.spring_project.digital_banking_system.model.Wallet;
import com.spring_project.digital_banking_system.repository.DataRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication service - Handles user registration, login, and logout
 * Uses simple session management for authentication
 */
@Service
public class AuthService {

    private final DataRepository dataRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.master-key:BANK_ADMIN_2025}")
    private String masterSecretKey;

    public AuthService(DataRepository dataRepository,
                       PasswordEncoder passwordEncoder) {
        this.dataRepository = dataRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, Object> register(Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");
        String roleStr = request.get("role");
        String masterKey = request.get("masterSecretKey");

        if (dataRepository.findUserByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        Role role = Role.USER;
        if ("ADMIN".equalsIgnoreCase(roleStr)) {
            if (masterKey == null || !masterKey.equals(masterSecretKey)) {
                throw new IllegalArgumentException("Invalid master secret key for admin registration");
            }
            role = Role.ADMIN;
        }

        User user = new User(
                username,
                email,
                passwordEncoder.encode(password),
                role
        );

        dataRepository.saveUser(user);

        Wallet wallet = new Wallet(user.getId());
        dataRepository.saveWallet(wallet);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        response.put("walletCode", wallet.getWalletCode());
        return response;
    }

    public Map<String, Object> login(Map<String, String> request, HttpServletRequest httpRequest) {
        String username = request.get("username");
        String password = request.get("password");

        Optional<User> userOpt = dataRepository.findUserByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // Create session and store user info
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole().name());

        // Set Spring Security context
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Optional<Wallet> walletOpt = dataRepository.findWalletByUserId(user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        response.put("sessionId", session.getId());
        if (walletOpt.isPresent()) {
            response.put("walletCode", walletOpt.get().getWalletCode());
        }
        return response;
    }

    public Map<String, Object> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logout successful");
        return response;
    }

    public Long getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                return userId;
            }
        }
        throw new IllegalStateException("No active session found");
    }
}