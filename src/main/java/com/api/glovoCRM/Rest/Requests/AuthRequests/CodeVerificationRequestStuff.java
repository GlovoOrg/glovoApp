package com.api.glovoCRM.Rest.Requests.AuthRequests;

import lombok.Data;

@Data
public class CodeVerificationRequestStuff {
    private String login;
    private String code;
}
