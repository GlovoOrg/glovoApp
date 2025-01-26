package com.api.glovoCRM.Models.UserModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refreshTokens")
@ToString
public class RefreshToken extends BaseEntity {

    //todo в будущем надо спросить у степана
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @NotNull(message = "Токен не может быть null")
    @NotBlank(message = "Токен обязателен")
    @Column(nullable = false, name = "token")
    private String token;

    @Future(message = "Дата истечения должна быть в будущем")
    @Column(nullable = false, name = "expiryDate")
    private Instant expiryDate;
}
