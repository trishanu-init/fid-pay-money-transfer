package com.fidpay.moneytransfer.service;

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
    public void sendTransactionNotification(
            String toEmail,
            String recipientName,
            String transactionType,
            BigDecimal amount,
            String accountNumber,
            BigDecimal availableBalance,
            String transactionDate,
            String counterpartyName,
            String counterpartyAccountId,
            String transactionId) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("Fid-Pay Money Transfer: " + transactionType.toUpperCase() + " of ₹" + amount);

            // Determine styling based on transaction type (DEBIT for sender, CREDIT for
            // receiver)
            boolean isDebit = transactionType.equalsIgnoreCase("DEBIT");
            String statusColor = isDebit ? "#dc3545" : "#28a745";
            String preposition = isDebit ? "from" : "in";
            String counterpartyLabel = isDebit ? "To:" : "From:";

            String htmlBody = String.format(
                    """
                            <html>
                            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 500px; margin: auto; border: 1px solid #eee; padding: 20px;">
                                <div style="text-align: center; border-bottom: 2px solid #004a99; padding-bottom: 10px;">
                                    <h2 style="color: #004a99; margin: 0;">Fid-Pay</h2>
                                </div>
                                <div style="padding: 20px 0;">
                                    <p>Hi <strong>%s</strong>,</p>
                                    <p><strong>₹%.2f</strong> <span style="color: %s; font-weight: bold;">%s</span> via money transfer %s your Fid-Pay account <strong>#%s</strong>.</p>
                                    <p style="background-color: #f8f9fa; padding: 10px; border-left: 4px solid #004a99;">
                                        <strong>Available Bal. ₹%.2f</strong>
                                    </p>
                                    <table style="width: 100%%; font-size: 0.9em; color: #555; margin-top: 20px;">
                                        <tr><td><strong>Transaction Date:</strong></td><td>%s</td></tr>
                                        <tr><td><strong>%s</strong></td><td>%s (%s)</td></tr>
                                        <tr><td><strong>Transaction ID:</strong></td><td>%s</td></tr>
                                    </table>
                                </div>
                                <div style="margin-top: 30px; font-size: 0.9em; border-top: 1px solid #eee; padding-top: 10px;">
                                    <p>Best,<br/><strong>Team Fid-Pay</strong></p>
                                </div>
                            </body>
                            </html>
                            """,
                    recipientName, amount, statusColor, transactionType.toUpperCase(), preposition, accountNumber,
                    availableBalance, transactionDate, counterpartyLabel, counterpartyName, counterpartyAccountId,
                    transactionId);

            helper.setText(htmlBody, true); // true = send as HTML

            javaMailSender.send(message);
            log.info("Email sent successfully to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send email", e);
        }
    }

    /**
     * Send OTP email to user
     */
    @Async
    public void sendOtpEmail(String toEmail, String recipientName, String otp) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("Fid-Pay: Your OTP for Money Transfer");

            String htmlBody = String.format(
                    """
                            <html>
                            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 500px; margin: auto; border: 1px solid #eee; padding: 20px;">
                                <div style="text-align: center; border-bottom: 2px solid #004a99; padding-bottom: 10px;">
                                    <h2 style="color: #004a99; margin: 0;">Fid-Pay</h2>
                                </div>
                                <div style="padding: 20px 0; text-align: center;">
                                    <p>Hi <strong>%s</strong>,</p>
                                    <p>Your One-Time Password (OTP) for verifying your money transfer is:</p>
                                    <div style="background: linear-gradient(135deg, #004a99 0%%, #0066cc 100%%); padding: 20px; border-radius: 10px; margin: 20px 0;">
                                        <span style="font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #ffffff;">%s</span>
                                    </div>
                                    <p style="color: #666; font-size: 0.9em;">This OTP is valid for <strong>5 minutes</strong>.</p>
                                    <p style="color: #dc3545; font-size: 0.85em;">⚠️ Do not share this OTP with anyone.</p>
                                </div>
                                <div style="margin-top: 30px; font-size: 0.9em; border-top: 1px solid #eee; padding-top: 10px;">
                                    <p>If you didn't request this OTP, please ignore this email or contact support.</p>
                                    <p>Best,<br/><strong>Team Fid-Pay</strong></p>
                                </div>
                            </body>
                            </html>
                            """,
                    recipientName, otp);

            helper.setText(htmlBody, true);

            javaMailSender.send(message);
            log.info("OTP email sent successfully to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send OTP email", e);
        }
    }

    /**
     * Send forgot password OTP email to user
     */
    @Async
    public void sendForgotPasswordOtpEmail(String toEmail, String recipientName, String otp) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("Fid-Pay: Your OTP for Password Reset");

            String htmlBody = String.format(
                    """
                            <html>
                            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 500px; margin: auto; border: 1px solid #eee; padding: 20px;">
                                <div style="text-align: center; border-bottom: 2px solid #004a99; padding-bottom: 10px;">
                                    <h2 style="color: #004a99; margin: 0;">Fid-Pay</h2>
                                </div>
                                <div style="padding: 20px 0; text-align: center;">
                                    <p>Hi <strong>%s</strong>,</p>
                                    <p>Your One-Time Password (OTP) for resetting your password is:</p>
                                    <div style="background: linear-gradient(135deg, #004a99 0%%, #0066cc 100%%); padding: 20px; border-radius: 10px; margin: 20px 0;">
                                        <span style="font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #ffffff;">%s</span>
                                    </div>
                                    <p style="color: #666; font-size: 0.9em;">This OTP is valid for <strong>2 minutes</strong>.</p>
                                    <p style="color: #dc3545; font-size: 0.85em;">⚠️ Do not share this OTP with anyone.</p>
                                </div>
                                <div style="margin-top: 30px; font-size: 0.9em; border-top: 1px solid #eee; padding-top: 10px;">
                                    <p>If you didn't request this password reset, please ignore this email or contact support.</p>
                                    <p>Best,<br/><strong>Team Fid-Pay</strong></p>
                                </div>
                            </body>
                            </html>
                            """,
                    recipientName, otp);

            helper.setText(htmlBody, true);

            javaMailSender.send(message);
            log.info("Forgot password OTP email sent successfully to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send forgot password OTP email", e);
        }
    }
}