package me.fivi.service

import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
@Singleton
class EmailService {
    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class)

    @Value('${email.from}')
    private String fromEmail

    @Value('${email.confirmation.url}')
    private String confirmationBaseUrl

    // In a real application, you would inject an email client
    // Here we'll just log the emails for demonstration purposes

    void sendEmailConfirmation(String toEmail, String token) {
        String confirmationLink = "${confirmationBaseUrl}?token=${token}"
        String subject = "Confirm your email address"
        String body = """
            Hello,
            
            Thank you for signing up! Please confirm your email address by clicking the link below:
            
            ${confirmationLink}
            
            This link will expire in 24 hours.
            
            If you did not create an account, you can ignore this email.
            
            Best regards,
            Your Mobile App Team
        """

        sendEmail(toEmail, subject, body)
    }

    void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = "${confirmationBaseUrl}/reset-password?token=${token}"
        String subject = "Reset your password"
        String body = """
            Hello,
            
            We received a request to reset your password. Click the link below to create a new password:
            
            ${resetLink}
            
            This link will expire in 1 hour.
            
            If you did not request a password reset, you can ignore this email.
            
            Best regards,
            Your Mobile App Team
        """

        sendEmail(toEmail, subject, body)
    }

    private void sendEmail(String toEmail, String subject, String body) {
        // In a real application, you would use a proper email service
        // For now, just log the email
        LOG.info("Sending email to: {}", toEmail)
        LOG.info("From: {}", fromEmail)
        LOG.info("Subject: {}", subject)
        LOG.info("Body: {}", body)

        // Implementation with a real email service would go here
        // Examples include SendGrid, AWS SES, JavaMail, etc.
    }
}
