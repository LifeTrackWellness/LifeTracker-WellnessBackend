package com.compa.service;

import com.compa.dto.request.LoginRequest;
import com.compa.dto.request.RegisterRequest;
import com.compa.dto.response.AuthResponse;
import com.compa.enums.OrientadorStatus;
import com.compa.exception.BusinessException;
import com.compa.exception.ResourceNotFoundException;
import com.compa.model.Estudiante;
import com.compa.model.Orientador;
import com.compa.repository.EstudianteRepository;
import com.compa.repository.OrientadorRepository;
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
    private final OrientadorRepository orientadorRepository;
    private final EstudianteRepository estudianteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public void register(RegisterRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Las contraseñas no coinciden");
        }

        if (orientadorRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Ya existe una cuenta con ese email");
        }

        String verificationToken = UUID.randomUUID().toString();

        Orientador orientador = Orientador.builder()
                .name(request.getName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(OrientadorStatus.PENDING)
                .verificationToken(verificationToken)
                .verificationTokenExpiresAt(LocalDateTime.now().plusHours(24))
                .build();

        orientadorRepository.save(orientador);

        try {
            emailService.sendVerificationEmail(
                    orientador.getEmail(),
                    orientador.getName(),
                    verificationToken);
        } catch (Exception e) {
            log.error("Error enviando email a {}: {}", orientador.getEmail(), e.getMessage());
        }
    }

    public void verifyEmail(String token) {
        Orientador orientador = orientadorRepository
                .findByVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token inválido o ya usado"));

        if (orientador.getVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("El token ha vencido. Solicita uno nuevo.");
        }

        orientador.setStatus(OrientadorStatus.ACTIVE);
        orientador.setVerificationToken(null);
        orientador.setVerificationTokenExpiresAt(null);

        orientadorRepository.save(orientador);
        log.info("Cuenta verificada: {}", orientador.getEmail());
    }

    public AuthResponse login(LoginRequest request) {

        // Buscar primero en orientadores
        Optional<Orientador> orientadorOpt = orientadorRepository.findByEmail(request.getEmail());
        if (orientadorOpt.isPresent()) {
            Orientador orientador = orientadorOpt.get();

            if (!passwordEncoder.matches(request.getPassword(), orientador.getPassword())) {
                throw new BusinessException("Credenciales inválidas");
            }

            if (orientador.getStatus() == OrientadorStatus.PENDING) {
                throw new BusinessException("Debes confirmar tu email antes de iniciar sesión");
            }

            String jwt = jwtService.generateToken(
                    orientador.getEmail(),
                    orientador.getRole().name());

            return AuthResponse.builder()
                    .token(jwt)
                    .type("Bearer")
                    .id(orientador.getId())
                    .name(orientador.getName())
                    .lastName(orientador.getLastName())
                    .email(orientador.getEmail())
                    .role(orientador.getRole().name())
                    .build();
        }

        // Buscar en estudiantes
        Estudiante estudiante = estudianteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));

        if (estudiante.getPassword() == null) {
            throw new BusinessException("Esta cuenta no tiene credenciales de acceso configuradas");

        }

        if (!passwordEncoder.matches(request.getPassword(), estudiante.getPassword())) {
            log.info(">>> Estudiante encontrado: {}", estudiante.getEmail());
            log.info(">>> Cuenta activada: {}", estudiante.isAccountActivated());
            log.info(">>> Password hash: {}", estudiante.getPassword());
            log.info(">>> Password ingresada: {}", request.getPassword());
            log.info(">>> Matches: {}", passwordEncoder.matches(request.getPassword(), estudiante.getPassword()));
            throw new BusinessException("Credenciales inválidas");

        }

        if (!estudiante.isAccountActivated()) {
            throw new BusinessException("Debes activar tu cuenta antes de iniciar sesión. Revisa tu email.");
        }

        String jwt = jwtService.generateToken(
                estudiante.getEmail(),
                estudiante.getRole().name());

        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(estudiante.getId())
                .name(estudiante.getName())
                .lastName(estudiante.getLastName())
                .email(estudiante.getEmail())
                .role(estudiante.getRole().name())
                .build();
    }

}