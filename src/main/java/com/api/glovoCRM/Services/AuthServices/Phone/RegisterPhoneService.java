package com.api.glovoCRM.Services.AuthServices.Phone;

import com.api.glovoCRM.Repositories.UserRepositories.UserRepository;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.AuthRequests.RegisterRequestPhone;
import com.api.glovoCRM.Rest.Responses.Auth.RegisterResponse;
import com.api.glovoCRM.Services.AuthServices.AuthService;
import com.api.glovoCRM.constants.EUserStatuses;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class RegisterPhoneService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Autowired
    public RegisterPhoneService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public RegisterResponse signUp(RegisterRequestPhone request){
        if (userRepository.existsByName(request.getName())) {
            throw new AlreadyExistsEx("Имя пользователя уже занято");
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AlreadyExistsEx("Номер телефона уже зарегистрирован");
        }
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setStatus(EUserStatuses.PENDING_LOGIN_TO_THE_SYSTEM);
        authService.assignDefaultRole(newUser);
        userRepository.save(newUser);
        log.info("Пользователь успешно сохранен с номером: {}", newUser.getPhoneNumber());

        return new RegisterResponse("Пользователь успешно добавлен в систему по номеру: " + newUser.getPhoneNumber());
    }
}
