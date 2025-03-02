package com.api.glovoCRM.Rest.Requests.UserRequests;

import com.api.glovoCRM.constants.EUserStatuses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminFindUserFilterRequest {
    private String username;
    private String email;
    private String phoneNumber;
    private EUserStatuses status;
    private Boolean isStaff;
    private String chatId;
}
