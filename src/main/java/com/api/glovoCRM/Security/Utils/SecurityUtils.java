package com.api.glovoCRM.Security.Utils;

import com.api.glovoCRM.Repositories.UserRepositories.UserRepository;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Security.Providers.EmailAuthProvider;
import com.api.glovoCRM.Security.Providers.PhoneAuthProvider;
import com.api.glovoCRM.Security.Providers.StuffAuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
public class SecurityUtils {

    private final UserRepository userRepository;

    @Autowired
    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Bean
    public PasswordEncoder getPasswordEncoder() {return new BCryptPasswordEncoder();}

    @Bean
    public UserDetailsService userDetailsService() {
        return identifier -> {
            User user = userRepository.findByName(identifier)
                    .or(() -> userRepository.findByEmail(identifier))
                    .or(() -> userRepository.findByPhoneNumber(identifier))
                    .or(() -> userRepository.findByLogin(identifier))
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
            if(!user.isEnabled()) {
                throw new RuntimeException("Аккаунт не активирован. Пожалуйста, подтвердите свою почту.");
            }
            return (UserDetails) user;
        };
    }
    @Bean
    public AuthenticationManager authenticationManager(StuffAuthProvider stuffAuthProvider, EmailAuthProvider emailAuthProvider, PhoneAuthProvider phoneAuthProvider) throws Exception {
        return new ProviderManager(Arrays.asList(phoneAuthProvider, emailAuthProvider, stuffAuthProvider));
    }
}
