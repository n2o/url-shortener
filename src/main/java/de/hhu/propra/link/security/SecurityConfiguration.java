package de.hhu.propra.link.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/*/delete").hasRole("ADMIN")
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().permitAll()
        );
        http.formLogin(form -> form
                .loginPage("/login")
                .permitAll()
        );
        http.logout(logout -> logout
                .permitAll()
        );
        return http.build();
    }
}
