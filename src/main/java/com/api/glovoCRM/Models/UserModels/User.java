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
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;



@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class User extends BaseEntity implements UserDetails {

    @NotNull(message = "Имя пользователя не может быть null")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Length(min = 3, max = 50, message = "Имя пользователя должно быть в диапозоне 3-50 символов")
//    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Имя пользователя может содержать только буквы, цифры и подчеркивания")
    @Column(name = "username", unique = true, nullable = false)
    @NaturalId
    private String name;

    @Email(message = "Невалидная почта")
    @Column(name = "email", unique = true, length = 122)
    @NaturalId
    private String email;

//    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Неверный формат номера телефона")
    @Column(name = "phone_number", unique = true, length = 20)
    @NaturalId
    private String phoneNumber;

//    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9]).{8,20}$", message = "Пароль должен содержать хотя бы одну заглавную букву, одну цифру и иметь длину от 8 до 20 символов")
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EUserStatuses status = EUserStatuses.INACTIVE;

    @Column(name = "lastLoginDate")
    @PastOrPresent(message = "Дата последнего входа должна быть в прошлом или настоящем")
    private LocalDateTime lastLoginDate;

    @Column(name = "login")
    private String login;

    @Column(name = "chatId")
    private String chatId;

//    @Transient
//    private String emailCode; //todo в redis
//
//    @Transient
//    private String phoneCode; //todo в redis

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id"))
    public Set<Role> roles = new HashSet<>();

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
        return name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
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
        return this.status == EUserStatuses.ACTIVE;
    }
    public void addSocialAccount(SocialAccount socialAccount) {
        log.info("Добавление социального аккаунта: provider={}", socialAccount.getProvider());
        if (this.socialAccounts == null) {
            this.socialAccounts = new ArrayList<>();
        }
        this.socialAccounts.add(socialAccount);
        socialAccount.setUser(this);
    }

}
