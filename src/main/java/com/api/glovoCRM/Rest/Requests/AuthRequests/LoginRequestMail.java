package com.api.glovoCRM.Rest.Requests.AuthRequests;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequestMail {
    @Email(message = "Только почту можно")
    private String email;
    private String password;
}
