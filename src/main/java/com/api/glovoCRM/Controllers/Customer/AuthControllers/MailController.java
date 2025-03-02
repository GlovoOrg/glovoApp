package com.api.glovoCRM.Controllers.Customer.AuthControllers;


import com.api.glovoCRM.Rest.Requests.AuthRequests.LoginRequestMail;
import com.api.glovoCRM.Rest.Requests.AuthRequests.RegisterRequestMail;
import com.api.glovoCRM.Rest.Requests.AuthRequests.ResendRequest;
import com.api.glovoCRM.Rest.Requests.AuthRequests.VerifyCodeRequestMail;
import com.api.glovoCRM.Rest.Responses.Auth.LoginResponse;
import com.api.glovoCRM.Rest.Responses.Auth.RegisterResponse;
import com.api.glovoCRM.Services.AuthServices.Mail.LoginServiceMail;
import com.api.glovoCRM.Services.AuthServices.Mail.RegisterMailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class MailController {

    private final RegisterMailService registerMailService;
    private final LoginServiceMail loginServiceMail;

    @PostMapping("/register-mail")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody @Valid RegisterRequestMail request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registerMailService.signUp(request));
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam String token) {
        registerMailService.confirmEmail(token);
        return ResponseEntity.ok("Email успешно подтвержден!");
    }
    @PostMapping("/tipa-login-mail")
    public ResponseEntity<?> initiateLogin(@RequestBody @Valid LoginRequestMail request) {
            loginServiceMail.TipaLogin(request);
            return ResponseEntity.ok().body(Map.of("message", "Код отправлен на email"));
    }
    @PostMapping("/verify-code-mail")
    public ResponseEntity<LoginResponse> verifyCode(
            @RequestBody @Valid VerifyCodeRequestMail request
    ) {
        LoginResponse response = loginServiceMail.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(response);
    }
    @PostMapping("/resend-confirmation-mail")
    public ResponseEntity<?> resendConfirmation(@RequestBody @Valid ResendRequest request) {
        registerMailService.resendConfirmationEmail(request.getEmail());
        return ResponseEntity.ok().body(Map.of("message", "Письмо с подтверждением отправлено повторно"));
    }

    @PostMapping("/resend-code-mail")
    public ResponseEntity<?> resendCode(@RequestBody @Valid ResendRequest request) {
        loginServiceMail.resendVerificationCode(request.getEmail());
        return ResponseEntity.ok().body(Map.of("message", "Код подтверждения отправлен повторно"));
    }
}
