package com.example.libraryapp.config;

import com.example.libraryapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .authorizeHttpRequests(auth -> auth
            .requestMatchers(
              "/", "/login", "/css/**", "/js/**",
              "/h2-console/**", "/api/**", "/ws/**"
            ).permitAll()
            .anyRequest().authenticated()
          )
          .formLogin(form -> form
            .loginPage("/login")
            //.defaultSuccessUrl("/", true)
                  .defaultSuccessUrl("/books", true)
            .permitAll()
          )
          .logout(logout -> logout.permitAll())
          .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/ws/**"))
          .headers(headers -> headers
              // zamiast deprecated frameOptions().disable()
              .frameOptions(frame -> frame.disable())
          );

        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
            .passwordEncoder(passwordEncoder());
    }
}
