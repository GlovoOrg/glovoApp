package com.api.glovoCRM.Models.UserModels;

import com.api.glovoCRM.Models.BaseEntity;
import com.api.glovoCRM.Models.OrderDetailModels.Cart;
import com.api.glovoCRM.Models.OrderDetailModels.Order;
import com.api.glovoCRM.constants.EUserStatuses;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


//todo В будущем повесить более детальные аннотации типа @Length @NotBlank @Email(для email) и тд
//todo также надо orphanremoval jsonbackreference(надо вспомнить, что эта за тема)
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails {

    @NotNull(message = "Имя пользователя не может быть null")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Length(min = 3, max = 50, message = "Имя пользователя должно быть в диапозоне 3-50 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Имя пользователя может содержать только буквы, цифры и подчеркивания")
    @Column(name = "username", unique = true, nullable = false)
    @NaturalId
    private String username;

    @NotNull(message = "Почта пользователя не может быть null")
    @Email(message = "Невалидная почта")
    @NotBlank(message = "Почта не может пустой")
    @Column(name = "email", unique = true, nullable = false, length = 122)
    @NaturalId
    private String email;

    @NotNull(message = "Пароль пользователя не может быть null")
    @NotBlank(message = "Пароль не может быть пустой")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9]).{8,20}$", message = "Пароль должен содержать хотя бы одну заглавную букву, одну цифру и иметь длину от 8 до 20 символов")
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EUserStatuses status;

    @Column(name = "lastLoginDate")
    @PastOrPresent(message = "Дата последнего входа должна быть в прошлом или настоящем")
    private LocalDateTime lastLoginDate;

    @NotNull(message = "Статус верификации email не может быть null")
    @Column(name = "isEmailVerified", nullable = false)
    private boolean emailVerified = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id"))
    public List<Role> roles = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user", orphanRemoval = true)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdTime desc")
    private List<Order> orderEntities = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    @PrePersist
    public void updateLastLogin() {
        this.lastLoginDate = LocalDateTime.now();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return lastLoginDate != null
                && lastLoginDate.plusMonths(3).isAfter(LocalDateTime.now());
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status != EUserStatuses.BLOCKED_BY_ADMIN;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return emailVerified && status != EUserStatuses.PENDING_EMAIL_VERIFICATION;
    }
}
