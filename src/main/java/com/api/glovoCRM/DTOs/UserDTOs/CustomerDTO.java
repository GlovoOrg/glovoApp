package com.api.glovoCRM.DTOs.UserDTOs;

import com.api.glovoCRM.Models.UserModels.SocialAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private List<SocialAccountDTO> socialAccounts;
}
