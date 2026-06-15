package com.wellness.backend.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${resend.api-key}")
    private String resendApiKey;

    private static final String FROM = "LifeTracker Wellness <onboarding@resend.dev>";

    @Async
    public void sendVerificationEmail(String toEmail, String name, String verificationToken) {
        String verificationLink = frontendUrl + "/verify-email?token=" + verificationToken;
        String body =
                "Hola " + name + ",\n\n" +
                        "Gracias por registrarte en LifeTracker Wellness.\n\n" +
                        "Confirma tu cuenta haciendo clic aquí:\n" +
                        verificationLink + "\n\n" +
                        "Este enlace vence en 24 horas.\n\n" +
                        "Si no creaste esta cuenta, ignora este mensaje.\n\n" +
                        "Equipo LifeTracker Wellness";
        send(toEmail, "Confirma tu cuenta en LifeTracker Wellness", body);
    }

    @Async
    public void sendPatientCredentials(String toEmail, String name,
                                       String tempPassword, String activationToken) {
        String activationLink = frontendUrl + "/activate?token=" + activationToken;
        String body =
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
                        "Equipo LifeTracker Wellness";
        send(toEmail, "Bienvenido a LifeTracker Wellness - Tus credenciales de acceso", body);
    }

    @Async
    public void sendRiskAlertEmail(String toEmail, String professionalName,
                                   String patientName, String description) {
        String body =
                "Hola " + professionalName + ",\n\n" +
                        "Tu paciente " + patientName + " ha sido identificado en nivel de riesgo ALTO.\n\n" +
                        "Descripción: " + description + "\n\n" +
                        "Te recomendamos revisar su estado lo antes posible en la plataforma.\n\n" +
                        "Equipo LifeTracker Wellness";
        send(toEmail, "⚠️ Alerta de riesgo alto — " + patientName, body);
    }

    private void send(String toEmail, String subject, String body) {
        try {
            Resend resend = new Resend(resendApiKey);
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(FROM)
                    .to(toEmail)
                    .subject(subject)
                    .text(body)
                    .build();
            resend.emails().send(params);
            log.info(">>> Email enviado a: {}", toEmail);
        } catch (ResendException e) {
            log.error(">>> Error enviando email a {}: {}", toEmail, e.getMessage());
        }
    }
}