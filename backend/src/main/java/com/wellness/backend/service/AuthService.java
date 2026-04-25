package com.wellness.backend.service;

import com.wellness.backend.dto.request.LoginRequest;
import com.wellness.backend.dto.request.RegisterRequest;
import com.wellness.backend.dto.response.AuthResponse;
import com.wellness.backend.enums.ProfessionalStatus;
import com.wellness.backend.exception.BusinessException;
import com.wellness.backend.exception.ResourceNotFoundException;
import com.wellness.backend.model.Professional;
import com.wellness.backend.repository.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final ProfessionalRepository professionalRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public void register(RegisterRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Las contraseñas no coinciden");
        }

        if (professionalRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Ya existe una cuenta con ese email");
        }

        String verificationToken = UUID.randomUUID().toString();

        Professional professional = Professional.builder()
                .name(request.getName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(ProfessionalStatus.PENDING)
                .verificationToken(verificationToken)
                .verificationTokenExpiresAt(LocalDateTime.now().plusHours(24))
                .build();

        professionalRepository.save(professional);

        try {
            emailService.sendVerificationEmail(
                    professional.getEmail(),
                    professional.getName(),
                    verificationToken
            );
        } catch (Exception e) {
            log.error("Error enviando email a {}: {}", professional.getEmail(), e.getMessage());
        }
    }

    public void verifyEmail(String token) {
        Professional professional = professionalRepository
                .findByVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token inválido o ya usado"));

        if (professional.getVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("El token ha vencido. Solicita uno nuevo.");
        }

        professional.setStatus(ProfessionalStatus.ACTIVE);
        professional.setVerificationToken(null);
        professional.setVerificationTokenExpiresAt(null);

        professionalRepository.save(professional);
        log.info("Cuenta verificada: {}", professional.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        Professional professional = professionalRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), professional.getPassword())) {
            throw new BusinessException("Credenciales inválidas");
        }

        if (professional.getStatus() == ProfessionalStatus.PENDING) {
            throw new BusinessException("Debes confirmar tu email antes de iniciar sesión");
        }

        String jwt = jwtService.generateToken(
                professional.getEmail(),
                professional.getRole().name());

        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(professional.getId())
                .name(professional.getName())
                .lastName(professional.getLastName())
                .email(professional.getEmail())
                .role(professional.getRole().name())
                .build();
    }
}