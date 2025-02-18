package com.api.glovoCRM.Rest.Requests.AuthRequests;

import lombok.Data;

@Data
public class RegisterRequestStuff {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String login;
    private String role;
}
