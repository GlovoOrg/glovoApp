package com.api.glovoCRM.Rest.Requests.AuthRequests;

import lombok.Data;

@Data
public class RegisterRequestPhone {
    private String name;
    private String phoneNumber;
    private String password;
}
