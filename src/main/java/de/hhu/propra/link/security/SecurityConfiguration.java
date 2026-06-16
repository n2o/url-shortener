package de.hhu.propra.link.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/").hasRole("ADMIN")
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure the single admin account from the environment.
     *
     * <p>The password is never stored in plain text: it is hashed with BCrypt before being held in
     * memory. There is intentionally no default password — the application refuses to start unless a
     * non-empty {@code SHORTY_ADMIN_PASSWORD} is provided, so a deployment can never silently fall
     * back to a weak, well-known credential.
     */
    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder,
            @Value("${SHORTY_ADMIN:admin}") String adminUsername,
            @Value("${SHORTY_ADMIN_PASSWORD:}") String adminPassword) {
        if (!StringUtils.hasText(adminPassword)) {
            throw new IllegalStateException(
                    "No admin password configured. Set the SHORTY_ADMIN_PASSWORD environment variable "
                            + "to a strong, non-empty value before starting the application.");
        }
        UserDetails admin = User.withUsername(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }
}
