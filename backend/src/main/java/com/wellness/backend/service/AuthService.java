package com.wellness.backend.service;

import com.wellness.backend.dto.request.LoginRequest;
import com.wellness.backend.dto.request.RegisterRequest;
import com.wellness.backend.dto.response.AuthResponse;
import com.wellness.backend.enums.ProfessionalStatus;
import com.wellness.backend.exception.BusinessException;
import com.wellness.backend.exception.ResourceNotFoundException;
import com.wellness.backend.model.Patient;
import com.wellness.backend.model.Professional;
import com.wellness.backend.repository.PatientRepository;
import com.wellness.backend.repository.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final ProfessionalRepository professionalRepository;
    private final PatientRepository patientRepository;
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
                    verificationToken);
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

        // Buscar primero en profesionales
        Optional<Professional> professionalOpt = professionalRepository.findByEmail(request.getEmail());
        if (professionalOpt.isPresent()) {
            Professional professional = professionalOpt.get();

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

        // Buscar en pacientes
        Patient patient = patientRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));

        if (patient.getPassword() == null) {
            throw new BusinessException("Esta cuenta no tiene credenciales de acceso configuradas");
        }

        if (!passwordEncoder.matches(request.getPassword(), patient.getPassword())) {
            throw new BusinessException("Credenciales inválidas");
        }

        if (!patient.isAccountActivated()) {
            throw new BusinessException("Debes activar tu cuenta antes de iniciar sesión. Revisa tu email.");
        }

        String jwt = jwtService.generateToken(
                patient.getEmail(),
                patient.getRole().name());

        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(patient.getId())
                .name(patient.getName())
                .lastName(patient.getLastName())
                .email(patient.getEmail())
                .role(patient.getRole().name())
                .build();
    }

}