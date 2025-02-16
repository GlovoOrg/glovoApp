package com.api.glovoCRM.Rest.Requests.AuthRequests;

import lombok.Data;

@Data
public class VerifyCodeRequestMail {
    private String email;
    private String code;
}
