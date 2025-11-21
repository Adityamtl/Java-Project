package com.spring_project.digital_banking_system.service;

import com.spring_project.digital_banking_system.dto.LoginRequest;
import com.spring_project.digital_banking_system.dto.RegisterRequest;
import com.spring_project.digital_banking_system.model.Role;
import com.spring_project.digital_banking_system.model.User;
import com.spring_project.digital_banking_system.model.Wallet;
import com.spring_project.digital_banking_system.repository.UserRepository;
import com.spring_project.digital_banking_system.repository.WalletRepository;
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

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.master-key:BANK_ADMIN_2025}")
    private String masterSecretKey;

    public AuthService(UserRepository userRepository,
                       WalletRepository walletRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, Object> register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        Role role = Role.USER;
        if ("ADMIN".equalsIgnoreCase(request.getRole())) {
            if (request.getMasterSecretKey() == null ||
                    !request.getMasterSecretKey().equals(masterSecretKey)) {
                throw new IllegalArgumentException("Invalid master secret key for admin registration");
            }
            role = Role.ADMIN;
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                role
        );

        userRepository.save(user);

        Wallet wallet = new Wallet(user.getId());
        walletRepository.save(wallet);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        response.put("walletCode", wallet.getWalletCode());
        return response;
    }

    public Map<String, Object> login(LoginRequest request, HttpServletRequest httpRequest) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
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

        Optional<Wallet> walletOpt = walletRepository.findByUserId(user.getId());

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