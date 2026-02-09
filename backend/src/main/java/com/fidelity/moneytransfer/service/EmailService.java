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
}