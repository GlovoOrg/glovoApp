package com.api.glovoCRM.Controllers.Customer.AuthControllers;

import com.api.glovoCRM.Rest.Responses.Auth.oauth2Response;
import com.api.glovoCRM.Services.AuthServices.Oauth2.CustomOauth2UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/oauth2")
@Slf4j
public class OAuth2Controller {
    private  final CustomOauth2UserService customOauth2UserService;

    @Autowired
    public OAuth2Controller(CustomOauth2UserService customOauth2UserService) {
        this.customOauth2UserService = customOauth2UserService;
    }

}
