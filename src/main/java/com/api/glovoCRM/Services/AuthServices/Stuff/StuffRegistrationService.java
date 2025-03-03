package com.api.glovoCRM.Services.AuthServices.Stuff;

import com.api.glovoCRM.Repositories.UserDAOs.UserRepository;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.AuthRequests.RegisterRequestStuff;
import com.api.glovoCRM.Services.AuthServices.AuthService;
import com.api.glovoCRM.constants.ERoles;
import com.api.glovoCRM.constants.EUserStatuses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class StuffRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Autowired
    public StuffRegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @Transactional
    public String registrationStuff(RegisterRequestStuff request) {
        if (userRepository.existsByName(request.getName())) {
            throw new AlreadyExistsEx("User with this name already exists");
        }
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsEx("User with this email already exists");
        }
        if(userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AlreadyExistsEx("User with this phone number already exists");
        }
        if(userRepository.existsByLogin(request.getLogin())){
            throw new AlreadyExistsEx("User with this login already exists");
        }
        User user = new User();
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setLogin(request.getLogin());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setStatus(EUserStatuses.PENDING_LOGIN_TO_THE_SYSTEM);
        authService.assignDefaultRoleStuff(user, ERoles.valueOf(request.getRole()));
        userRepository.save(user);
        log.info("Пользователь {} успешно зарегистрирован", request.getLogin());
        return "Пользователь с логином " + request.getLogin() + " успешно зарегистрирован. Привяжите Telegram через бота.";
    }
}
