package com.api.glovoCRM.Models.UserModels;

import com.api.glovoCRM.Models.BaseEntity;
import com.api.glovoCRM.Models.OrderDetailModels.Cart;
import com.api.glovoCRM.Models.OrderDetailModels.Order;
import com.api.glovoCRM.constants.EUserStatuses;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EUserStatuses status;

    private LocalDateTime lastLoginDate;

    private boolean emailVerified;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id"))
    public Set<Role> roles = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user", orphanRemoval = true)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Order> orders = new ArrayList<>();

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
                .map(role -> new SimpleGrantedAuthority(role.getName().name())) // Используем .name() для ERoles
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
