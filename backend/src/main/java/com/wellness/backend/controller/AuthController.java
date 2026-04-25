package com.wellness.backend.controller;

import com.wellness.backend.dto.request.LoginRequest;
import com.wellness.backend.dto.request.RegisterRequest;
import com.wellness.backend.dto.response.AuthResponse;
import com.wellness.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController
{
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message",
                        "Registro exitoso. Revisa tu email para confirmar tu cuenta."));
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyEmail(
            @RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(Map.of("message",
                "Email confirmado. Ya puedes iniciar sesión."));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
