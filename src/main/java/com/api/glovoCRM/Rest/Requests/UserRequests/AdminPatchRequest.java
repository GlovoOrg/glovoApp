package com.api.glovoCRM.Rest.Requests.UserRequests;

import com.api.glovoCRM.constants.EUserStatuses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminPatchRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
    private EUserStatuses status;
    private String login;
    private Boolean isStaff;
}
