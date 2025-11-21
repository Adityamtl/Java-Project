package com.spring_project.digital_banking_system.controller;

import com.spring_project.digital_banking_system.dto.LoginRequest;
import com.spring_project.digital_banking_system.dto.RegisterRequest;
import com.spring_project.digital_banking_system.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        Map<String, Object> response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request,
                                                     HttpServletRequest httpRequest) {
        Map<String, Object> response = authService.login(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        Map<String, Object> response = authService.logout(request);
        return ResponseEntity.ok(response);
    }
}