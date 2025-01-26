package com.api.glovoCRM.Models.UserModels;

import com.api.glovoCRM.Models.BaseEntity;
import com.api.glovoCRM.constants.AuthProviders;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "social_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialAccount extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "providers", nullable = false)
    private AuthProviders provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "access_token")
    private String accessToken;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
