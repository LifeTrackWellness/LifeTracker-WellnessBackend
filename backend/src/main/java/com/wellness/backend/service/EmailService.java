package com.wellness.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail, String name, String verificationToken) {
        String verificationLink = frontendUrl + "/verify-email?token=" + verificationToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Confirma tu cuenta en LifeTracker Wellness");
        message.setText(
                "Hola " + name + ",\n\n" +
                        "Gracias por registrarte en LifeTracker Wellness.\n\n" +
                        "Confirma tu cuenta haciendo clic aquí:\n" +
                        verificationLink + "\n\n" +
                        "Este enlace vence en 24 horas.\n\n" +
                        "Si no creaste esta cuenta, ignora este mensaje.\n\n" +
                        "Equipo LifeTracker Wellness");

        mailSender.send(message);
    }

    public void sendPatientCredentials(String toEmail, String name, String tempPassword, String activationToken) {
        String activationLink = frontendUrl + "/activate?token=" + activationToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Bienvenido a LifeTracker Wellness - Tus credenciales de acceso");
        message.setText(
                "Hola " + name + ",\n\n" +
                        "Tu profesional te ha registrado en LifeTracker Wellness.\n\n" +
                        "Tus credenciales temporales son:\n" +
                        "- Email: " + toEmail + "\n" +
                        "- Contraseña temporal: " + tempPassword + "\n\n" +
                        "Por favor activa tu cuenta haciendo clic aquí:\n" +
                        activationLink + "\n\n" +
                        "Este enlace vence en 48 horas.\n\n" +
                        "Una vez activada tu cuenta podrás cambiar tu contraseña.\n\n" +
                        "Si no esperabas este mensaje, ignóralo.\n\n" +
                        "Equipo LifeTracker Wellness");

        mailSender.send(message);
    }

}
