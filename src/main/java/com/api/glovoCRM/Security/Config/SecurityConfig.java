package com.api.glovoCRM.Security.Config;

import com.api.glovoCRM.Security.Providers.EmailAuthProvider;
//import com.api.glovoCRM.Security.Providers.PhoneAuthProvider;
import com.api.glovoCRM.Security.Providers.PhoneAuthProvider;
import com.api.glovoCRM.Services.AuthServices.Oauth2.CustomOauth2UserService;
import com.api.glovoCRM.Security.jwt.Filter.JwtAuthenticationFilter;
import com.api.glovoCRM.Services.AuthServices.Oauth2.OAuth2SuccessHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final PhoneAuthProvider phoneAuthProvider;
    private final EmailAuthProvider emailAuthProvider;
    private final CustomOauth2UserService customOauth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(PhoneAuthProvider phoneAuthProvider,UserDetailsService userDetailsService, OAuth2SuccessHandler auth2SuccessHandler, EmailAuthProvider emailAuthProvider, CustomOauth2UserService customOauth2UserService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.phoneAuthProvider = phoneAuthProvider;
        this.emailAuthProvider = emailAuthProvider;
        this.customOauth2UserService = customOauth2UserService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oAuth2SuccessHandler = auth2SuccessHandler;
        this.userDetailsService = userDetailsService;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/", "/non-secured/**", "api/v1/auth/**", "/oauth2/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/login/oauth2/code/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 ->
                        oauth2.loginPage("/login")
                                .failureUrl("/api/v1/error?error=Login failed")
                                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(customOauth2UserService))
                                .successHandler(oAuth2SuccessHandler))
                .logout(logout -> logout.logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true))
                .formLogin(Customizer.withDefaults())
                .authenticationProvider(phoneAuthProvider)
                .authenticationProvider(emailAuthProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
