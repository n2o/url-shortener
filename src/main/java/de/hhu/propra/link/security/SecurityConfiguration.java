package de.hhu.propra.link.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/*/delete").hasRole("ADMIN")
                .antMatchers("/admin").hasRole("ADMIN");
        http.formLogin()
                .loginPage("/login")
                .permitAll();
        http.logout()
                .permitAll();
    }
}

