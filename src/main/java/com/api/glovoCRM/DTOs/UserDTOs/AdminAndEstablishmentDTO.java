package com.api.glovoCRM.DTOs.UserDTOs;

import com.api.glovoCRM.Models.UserModels.Role;
import com.api.glovoCRM.Models.UserModels.SocialAccount;
import com.api.glovoCRM.constants.EUserStatuses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminAndEstablishmentDTO {
    private Long id;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String name;
    private String email;
    private LocalDateTime lastLoginDate;
    private String phoneNumber;
    private EUserStatuses status;
    private String login;
    private String chatId;
    private Set<Role> roles;
    private List<SocialAccount> socialAccounts;
}
