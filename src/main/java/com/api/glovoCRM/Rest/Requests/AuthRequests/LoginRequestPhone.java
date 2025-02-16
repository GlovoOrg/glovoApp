package com.api.glovoCRM.Rest.Requests.AuthRequests;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequestPhone {
    private String phoneNumber;
    private String password;
}
