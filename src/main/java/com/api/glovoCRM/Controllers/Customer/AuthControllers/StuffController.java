package com.api.glovoCRM.Controllers.Customer.AuthControllers;

import com.api.glovoCRM.Rest.Requests.AuthRequests.CodeVerificationRequestStuff;
import com.api.glovoCRM.Rest.Requests.AuthRequests.LoginRequestStuff;
import com.api.glovoCRM.Rest.Requests.AuthRequests.RegisterRequestStuff;
import com.api.glovoCRM.Rest.Responses.Auth.LoginResponse;
import com.api.glovoCRM.Rest.Responses.Auth.RegisterResponse;
import com.api.glovoCRM.Services.AuthServices.VerificationCodeService;
import com.api.glovoCRM.Services.AuthServices.Stuff.StuffLoginService;
import com.api.glovoCRM.Services.AuthServices.Stuff.StuffRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/stuff")
public class StuffController {
    private final StuffRegistrationService stuffRegistrationService;
    private final StuffLoginService stuffLoginService;
    private final VerificationCodeService verificationCodeService;

    public StuffController(StuffRegistrationService stuffRegistrationService, StuffLoginService stuffLoginService, VerificationCodeService verificationCodeService) {
        this.stuffRegistrationService = stuffRegistrationService;
        this.stuffLoginService = stuffLoginService;
        this.verificationCodeService = verificationCodeService;
    }
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerStuff(@RequestBody RegisterRequestStuff request) {
        String response = stuffRegistrationService.registrationStuff(request);
        return ResponseEntity.status(201).body(RegisterResponse.builder().message(response).build());
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequestStuff request
    ) {
        String message = stuffLoginService.TipaLoginStuff(
                request.getLogin(),
                request.getPassword()
        );
        return ResponseEntity.ok(message);
    }
    @PostMapping("/verify")
    public ResponseEntity<LoginResponse> verifyCode(
            @RequestBody CodeVerificationRequestStuff request
    ) {
        LoginResponse response = stuffLoginService.finalochkaLogin(
                request.getLogin(),
                request.getCode()
        );
        return ResponseEntity.ok(response);
    }
}
