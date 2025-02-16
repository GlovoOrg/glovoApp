package com.api.glovoCRM.Rest.Responses.Auth;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class oauth2Response {
    private String access_token;
    private String refresh_token;
    private String message;

    public oauth2Response(String access_token, String refresh_token) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }
    public oauth2Response(String message) {
        this.message = message;
    }
}
