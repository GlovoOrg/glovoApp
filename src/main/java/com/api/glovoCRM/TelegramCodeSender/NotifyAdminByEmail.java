package com.api.glovoCRM.TelegramCodeSender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotifyAdminByEmail {

    private final String adminEmail;
    private final JavaMailSender javaMailSender;

    @Autowired
    public NotifyAdminByEmail(JavaMailSender javaMailSender, @Value("${admin.email}") String adminEmail) {
        this.javaMailSender = javaMailSender;
        this.adminEmail = adminEmail;
    }

    public void notifyAdminByEmail(String errorMessage) {
        try {
            String safeMessage = StringEscapeUtils.escapeHtml4(errorMessage);
            MimeMessage message = javaMailSender.createMimeMessage();
            message.setSubject("Критическая ошибка в системе");
            message.setText("<html><body><h2>Произошла критическая ошибка:</h2><p>" + safeMessage + "</p></body></html>",
                    "UTF-8", "html");
            message.setRecipients(MimeMessage.RecipientType.TO, adminEmail);

            javaMailSender.send(message);
            log.info("Уведомление администратора отправлено на: {}", adminEmail);
        } catch (MessagingException e) {
            log.error("Ошибка отправки уведомления администратору: {}", e.getMessage());
        }
    }
}
