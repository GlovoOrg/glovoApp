package com.api.glovoCRM.Security.Providers;

import com.api.glovoCRM.Repositories.UserRepositories.UserRepository;
import com.api.glovoCRM.Exceptions.AuthExceptions.InvalidCredentialsEx;
import com.api.glovoCRM.Exceptions.AuthExceptions.UserNotVerifiedEx;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Security.AuthenticationTokens.EmailAuthenticationToken;
import com.api.glovoCRM.Services.AuthServices.Mail.EmailService;
import com.api.glovoCRM.Services.AuthServices.VerificationCodeService;
import com.api.glovoCRM.constants.EUserStatuses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailAuthProvider implements AuthenticationProvider {
    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        log.info("Попытка аутентификации пользователя с email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден с почтой: " + email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("Пароль неправильный для пользователя с почтой: {}", email);
            throw new InvalidCredentialsEx("Неверный пароль");
        }
        if (user.getStatus() == EUserStatuses.PENDING_EMAIL_VERIFICATION) {
            log.error("Пользователю с email: {} нужно подтвердить свою почту", email);
            emailService.sendVerificationEmail(email);
            throw new UserNotVerifiedEx("Аккаунт не подтвержден");
        }
        String code = verificationCodeService.generateCode();
        verificationCodeService.saveAuthCode(email, code);
        emailService.sendAuthCodeEmail(email, code);
        log.info("Отправлен 6 значный код для пользователя с почтой: {}", email);

        return EmailAuthenticationToken.preAuthenticated(email, null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmailAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
