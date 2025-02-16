package com.api.glovoCRM.Controllers.Customer.AuthControllers;

import com.api.glovoCRM.Rest.Requests.AuthRequests.LoginRequestPhone;
import com.api.glovoCRM.Rest.Requests.AuthRequests.RegisterRequestPhone;
import com.api.glovoCRM.Rest.Requests.AuthRequests.VerifyCodeRequestMail;
import com.api.glovoCRM.Rest.Requests.AuthRequests.VerifyCodeRequestPhone;
import com.api.glovoCRM.Rest.Responses.Auth.LoginResponse;
import com.api.glovoCRM.Rest.Responses.Auth.RegisterResponse;
import com.api.glovoCRM.Services.AuthServices.Phone.LoginPhoneService;
import com.api.glovoCRM.Services.AuthServices.Phone.RegisterPhoneService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
public class PhoneController {
    private final RegisterPhoneService registerPhoneService;
    private final LoginPhoneService loginPhoneService;

    public PhoneController(RegisterPhoneService registerPhoneService, LoginPhoneService loginPhoneService) {
        this.registerPhoneService = registerPhoneService;
        this.loginPhoneService = loginPhoneService;
    }
    @PostMapping("/register-phone")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequestPhone request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registerPhoneService.signUp(request));
    }
    @PostMapping("/tipa-login-phone")
    public ResponseEntity<?> initiateLogin(@RequestBody @Valid LoginRequestPhone request) {
        loginPhoneService.TipaLoginPhone(request);
        return ResponseEntity.ok().body(Map.of("message", "Код отправлен на номер"));
    }
    @PostMapping("/verify-code-phone")
    public ResponseEntity<LoginResponse> verifyCode(
            @RequestBody @Valid VerifyCodeRequestPhone request
    ) {
        LoginResponse response = loginPhoneService.verifyCode(request.getPhoneNumber(), request.getCode());
        return ResponseEntity.ok(response);
    }
}
