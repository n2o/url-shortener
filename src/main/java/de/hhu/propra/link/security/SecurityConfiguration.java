package de.hhu.propra.link.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth) -> auth
            .requestMatchers("/*/delete").hasRole("ADMIN")
            .requestMatchers("/admin").hasRole("ADMIN")
            .anyRequest().authenticated());
        http.formLogin((form) -> form.loginPage("/login").permitAll());
        http.logout(LogoutConfigurer::permitAll);
        return http.build();
    }
}

