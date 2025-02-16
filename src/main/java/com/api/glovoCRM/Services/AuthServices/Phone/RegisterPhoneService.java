package com.api.glovoCRM.Services.AuthServices.Phone;

import com.api.glovoCRM.DAOs.UserDAOs.UserDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.AuthRequests.RegisterRequestPhone;
import com.api.glovoCRM.Rest.Responses.Auth.RegisterResponse;
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
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegisterPhoneService(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public RegisterResponse signUp(RegisterRequestPhone request){
        if (userDAO.existsByName(request.getName())) {
            throw new AlreadyExistsEx("Имя пользователя уже занято");
        }
        if (userDAO.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AlreadyExistsEx("Номер телефона уже зарегистрирован");
        }
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setStatus(EUserStatuses.PENDING_PHONE_VERIFICATION);
        userDAO.save(newUser);
        log.info("Пользователь успешно сохранен с номером: {}", newUser.getPhoneNumber());

        return new RegisterResponse("Пользователь успешно добавлен в систему по номеру: " + newUser.getPhoneNumber());
    }
}
