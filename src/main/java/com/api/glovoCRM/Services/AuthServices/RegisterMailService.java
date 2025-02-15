package com.api.glovoCRM.Services.AuthServices;

import com.api.glovoCRM.DAOs.UserDAOs.UserDAO;
import com.api.glovoCRM.Exceptions.AuthExceptions.JwtExceptions.InvalidTokenEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.AuthRequests.RegisterRequestMail;
import com.api.glovoCRM.Rest.Responses.Auth.RegisterResponse;
import com.api.glovoCRM.Services.AuthServices.Mail.EmailService;
import com.api.glovoCRM.Services.AuthServices.Mail.VerificationCodeService;
import com.api.glovoCRM.constants.EUserStatuses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterMailService {
    private final PasswordEncoder passwordEncoder;
    private final UserDAO userDAO;
    private final EmailService emailService;
    private final VerificationCodeService verificationService;
    //todo крч потом надо поменять, частичку не получится реализовать, тупой я
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RegisterResponse signUp(RegisterRequestMail request) {
        if (userDAO.existsByName(request.getName())) {
            throw new AlreadyExistsEx("Имя пользователя уже занято");
        }
        User existingUser = userDAO.findByEmail(request.getEmail()).orElse(null);
        if (existingUser != null) {
            if (existingUser.getPassword() != null && !existingUser.getPassword().isEmpty()) {
                throw new AlreadyExistsEx("Email уже зарегистрирован");
            }

            // Если пароль отсутствует, добавляем его к существующему пользователю
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
            existingUser.setStatus(EUserStatuses.ACTIVE);
            userDAO.save(existingUser);

            log.info("Пароль успешно добавлен для существующего пользователя: {}", request.getEmail());
            return new RegisterResponse("Пароль успешно добавлен для существующего аккаунта");
        }

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setStatus(EUserStatuses.PENDING_EMAIL_VERIFICATION);

        userDAO.save(newUser);
        log.info("Начата регистрация пользователя: {}", request.getEmail());

        emailService.sendVerificationEmail(request.getEmail());

        return new RegisterResponse("Для завершения регистрации проверьте вашу почту");
    }

    public void confirmEmail(String token) {
        String email = verificationService.getEmailByToken(token);
        if (email == null) {
            log.warn("Недействительный токен подтверждения: {}", token);
            throw new InvalidTokenEx("Неверный токен подтверждения");
        }

        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        user.setStatus(EUserStatuses.ACTIVE);
        user.setEmailVerified(true);
        userDAO.save(user);

        verificationService.removeConfirmationToken(token);
        log.info("Email подтвержден: {}", email);
    }

    public void resendVerificationCode(String email) {
        if (!userDAO.existsByEmail(email)) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        emailService.resendAuthCode(email);
        log.info("Повторная отправка кода для: {}", email);
    }
    public void resendConfirmationEmail(String email) {
        if (!userDAO.existsByEmail(email)) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        emailService.sendVerificationEmail(email);
        log.info("Повторная отправка подтверждения для: {}", email);
    }
}

