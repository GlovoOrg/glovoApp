package com.api.glovoCRM.Rest.Requests.AuthRequests;

import lombok.Data;

@Data
public class RegisterRequestMail {
    private String name;
    private String email;
    private String password;
}
