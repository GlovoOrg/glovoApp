package com.api.glovoCRM.Rest.Requests.UserRequests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPatchRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
}
