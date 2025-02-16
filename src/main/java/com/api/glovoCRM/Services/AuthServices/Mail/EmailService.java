package com.api.glovoCRM.Services.AuthServices.Mail;

import com.api.glovoCRM.DAOs.UserDAOs.UserDAO;
import com.api.glovoCRM.Exceptions.MailSendingEx;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final VerificationCodeService verificationService;
    private final UserDAO userDAO;

    public void sendVerificationEmail(String email) {
        String token = verificationService.generateConfirmationToken();
        verificationService.saveConfirmationToken(token, email);

        String confirmationLink = "http://localhost:8080/api/v1/auth/confirm?token=" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Подтверждение регистрации");
            helper.setText(getConfirmationEmailContent(confirmationLink), true);
            helper.addInline("glovoLogo", new FileSystemResource(new File("src/main/resources/static/images/glovo.png")));
            mailSender.send(message);
            log.info("Письмо с подтверждением отправлено на: {}", email);
        } catch (MessagingException e) {
            log.error("Ошибка отправки письма: {}", e.getMessage());
            throw new MailSendingEx("Ошибка отправки письма подтверждения");
        }
    }


    public void resendAuthCode(String email) {
        String code = verificationService.generateCode();
        verificationService.saveAuthCode(email, code);
        sendAuthCodeEmail(email, code);
        log.info("Код повторно отправлен: {}", email);
    }

    public void sendAuthCodeEmail(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Ваш код подтверждения");

            String htmlContent = "<html>"
                    + "<head>"
                    + "<style>"
                    + "body { font-family: Arial, sans-serif; background: linear-gradient(135deg, #f4f4f4, #eaeaea); margin: 0; padding: 0; text-align: center; }"
                    + ".container { max-width: 500px; margin: 40px auto; background: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0px 5px 15px rgba(0, 0, 0, 0.15); }"
                    + ".logo { max-width: 100px; margin-bottom: 20px; }"
                    + "h2 { color: #2c3e50; font-size: 22px; margin-bottom: 10px; }"
                    + "p { font-size: 16px; color: #555; line-height: 1.5; }"
                    + ".code-box { background: #f3f3f3; padding: 15px; border-radius: 8px; font-size: 24px; font-weight: bold; color: #333; letter-spacing: 2px; display: inline-block; margin: 20px 0; }"
                    + ".button { display: inline-block; background: #4CAF50; color: white; padding: 12px 25px; font-size: 16px; text-decoration: none; border-radius: 8px; font-weight: bold; box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2); transition: 0.3s ease; }"
                    + ".button:hover { background: #45a049; box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.25); }"
                    + ".footer { margin-top: 20px; font-size: 14px; color: #888; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class='container'>"
                    + "<img src='cid:glovoLogo' class='logo' alt='Glovo Logo'/>"
                    + "<h2>Ваш код подтверждения</h2>"
                    + "<p>Используйте этот код для подтверждения вашего email.</p>"
                    + "<div class='code-box'>" + code + "</div>"
                    + "<p>Введите этот код в соответствующее поле на сайте или в приложении.</p>"
                    + "<p class='footer'>Если вы не запрашивали этот код, просто проигнорируйте это письмо.</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            helper.setText(htmlContent, true);
            helper.addInline("glovoLogo", new FileSystemResource(new File("src/main/resources/static/images/glovo.png")));

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Ошибка отправки кода подтверждения: {}", e.getMessage());
            throw new MailSendingEx("Ошибка при отправке кода");
        }
    }

    private String getConfirmationEmailContent(String link) {
        return "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; background: linear-gradient(135deg, #f4f4f4, #eaeaea); margin: 0; padding: 0; }"
                + ".container { max-width: 600px; margin: 50px auto; background: #ffffff; padding: 30px; border-radius: 12px; box-shadow: 0px 5px 15px rgba(0, 0, 0, 0.15); text-align: center; }"
                + ".logo { max-width: 120px; margin-bottom: 20px; }"
                + "h2 { color: #2c3e50; font-size: 22px; margin-bottom: 10px; }"
                + "p { font-size: 16px; color: #555; line-height: 1.6; }"
                + ".divider { width: 80%; margin: 20px auto; height: 1px; background: #ddd; }"
                + ".button { display: inline-block; background: #4CAF50; color: white; padding: 15px 30px; font-size: 16px; font-weight: bold; text-decoration: none; border-radius: 8px; box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2); transition: 0.3s ease; }"
                + ".button:hover { background: #45a049; box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.25); }"
                + ".footer { margin-top: 20px; font-size: 14px; color: #888; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<img src='cid:glovoLogo' class='logo' alt='GLOVO API Logo'/>"
                + "<h2>Подтверждение регистрации</h2>"
                + "<p>Добро пожаловать в API GLOVO! Подтвердите ваш email, нажав на кнопку ниже. И запомните, что БАРСА ЧЕМПИОН</p>"
                + "<a href='" + link + "' class='button'>Подтвердить email</a>"
                + "<div class='divider'></div>"
                + "<p class='footer'>Если вы не регистрировались, проигнорируйте это письмо.</p>"
                + "</div>"
                + "</body>"
                + "</html>";
    }



}
