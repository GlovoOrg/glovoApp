package com.api.glovoCRM.Rest.Requests.AuthRequests;

import lombok.Data;

@Data
public class VerifyCodeRequestPhone {
    private String phoneNumber;
    private String code;
}
