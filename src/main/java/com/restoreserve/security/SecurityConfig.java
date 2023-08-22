package com.restoreserve.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.restoreserve.enums.RoleEnum;
import com.restoreserve.utils.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authz->authz
        .requestMatchers("/api/user/login")
            .permitAll()
        .requestMatchers("/api/user/logout")
            .permitAll()
        .requestMatchers("/swagger-ui")
            .permitAll()
        .requestMatchers("/api/user/appadmin/**")
            .hasAnyRole(RoleEnum.App_Admin.toString(),RoleEnum.Super_Admin.toString())
        .requestMatchers("/api/restaurant/customer/**")
            .hasAnyRole(RoleEnum.Customer.toString(),RoleEnum.App_Admin.toString(),RoleEnum.Super_Admin.toString())
        .requestMatchers("/api/restaurant/**")
            .hasAnyRole(RoleEnum.Restaurant_Admin.toString(),RoleEnum.App_Admin.toString(),RoleEnum.Super_Admin.toString())
        .requestMatchers("/api/reservation/appadmin/**")
            .hasAnyRole(RoleEnum.App_Admin.toString(),RoleEnum.Super_Admin.toString())
        .requestMatchers("/api/reservation/customer/create")
            .hasAnyRole(RoleEnum.Customer.toString(),RoleEnum.App_Admin.toString(),RoleEnum.Super_Admin.toString())
        .requestMatchers("/api/menu/restaurant/**")
            .hasAnyRole(RoleEnum.Restaurant_Admin.toString(),RoleEnum.App_Admin.toString(),RoleEnum.Super_Admin.toString())
        .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
