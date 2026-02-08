package com.fidelity.moneytransfer.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async
    public void sendTransactionNotification(String toEmail, String transactionId, BigDecimal amount, String status) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("Fidelity Money Transfer Update: " + status);

            String htmlBody = String.format("""
                <html>
                <body>
                    <h2>Transaction Update</h2>
                    <p>Your transaction with ID <strong>%s</strong> has resulted in: <span style="color:green; font-weight:bold;">%s</span></p>
                    <p><strong>Amount:</strong> $%.2f</p>
                    <br/>
                    <p>Thank you for using Fidelity Money Transfer.</p>
                </body>
                </html>
                """, transactionId, status, amount);

            helper.setText(htmlBody, true); // true = send as HTML

            javaMailSender.send(message);
            log.info("Email sent successfully to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send email", e);
        }
    }
}