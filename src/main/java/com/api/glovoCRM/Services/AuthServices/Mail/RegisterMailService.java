package com.api.glovoCRM.Services.AuthServices.Mail;

import com.api.glovoCRM.Repositories.UserRepositories.UserRepository;
import com.api.glovoCRM.Exceptions.AuthExceptions.JwtExceptions.InvalidTokenEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.AuthRequests.RegisterRequestMail;
import com.api.glovoCRM.Rest.Responses.Auth.RegisterResponse;
import com.api.glovoCRM.Services.AuthServices.VerificationCodeService;
import com.api.glovoCRM.Services.AuthServices.AuthService;
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
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final VerificationCodeService verificationService;
    private final AuthService authService;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RegisterResponse signUp(RegisterRequestMail request) {
        if (userRepository.existsByName(request.getName())) {
            throw new AlreadyExistsEx("Имя пользователя уже занято");
        }
        User existingUser = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (existingUser != null) {
            if (existingUser.getSocialAccounts() != null && !existingUser.getPassword().isEmpty()) {
                throw new AlreadyExistsEx("Email уже зарегистрирован");
            }
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(existingUser);

            log.info("Пароль успешно добавлен для существующего пользователя: {}", request.getEmail());
            return new RegisterResponse("Пароль успешно добавлен для существующего аккаунта");
        }

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setStatus(EUserStatuses.PENDING_EMAIL_VERIFICATION);
        authService.assignDefaultRole(newUser);

        userRepository.save(newUser);
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

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        user.setStatus(EUserStatuses.PENDING_LOGIN_TO_THE_SYSTEM);
        userRepository.save(user);

        verificationService.removeConfirmationToken(token);
        log.info("Email подтвержден: {}", email);
    }


    public void resendConfirmationEmail(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        emailService.sendVerificationEmail(email);
        log.info("Повторная отправка подтверждения для: {}", email);
    }
}

