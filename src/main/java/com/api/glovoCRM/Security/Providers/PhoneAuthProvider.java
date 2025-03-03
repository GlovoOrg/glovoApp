package com.api.glovoCRM.Security.Providers;

import com.api.glovoCRM.Repositories.UserDAOs.UserRepository;
import com.api.glovoCRM.Exceptions.AuthExceptions.InvalidCredentialsEx;
import com.api.glovoCRM.Exceptions.MailSendingEx;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Security.AuthenticationTokens.PhoneAuthenticationToken;

import com.api.glovoCRM.Services.AuthServices.VerificationCodeService;
import com.api.glovoCRM.Services.AuthServices.Phone.TwilioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class PhoneAuthProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;
    private final TwilioService twilioService;

    public PhoneAuthProvider(UserRepository userRepository,
                             PasswordEncoder passwordEncoder,
                             VerificationCodeService verificationCodeService,
                             TwilioService twilioService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationCodeService = verificationCodeService;
        this.twilioService = twilioService;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Вызов метода authenticate для токена: {}", authentication.getClass().getName());
        String phoneNumber = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        log.info("Попытка аутентификации по номеру: {}", phoneNumber);
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + phoneNumber));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("Неверный пароль для номера: {}", phoneNumber);
            throw new InvalidCredentialsEx("Неверный пароль");
        }

        String code = verificationCodeService.generateCode();
        verificationCodeService.saveAuthCode(phoneNumber, code);

        try{
            twilioService.sendSms(phoneNumber, code);
            log.info("Код отправлен на номер: {}", phoneNumber);
        }catch (Exception e){
            log.error("Ошибка при отправке SMS на номер {}: {}", phoneNumber, e.getMessage());
            throw new MailSendingEx("Не удалось отправить SMS");
        }

        return PhoneAuthenticationToken.authenticated(
                user.getPhoneNumber(),
                null,
                user.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PhoneAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
