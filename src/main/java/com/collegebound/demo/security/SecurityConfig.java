package com.collegebound.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.collegebound.demo.user.User;
import com.collegebound.demo.user.UserRepository;

import jakarta.servlet.http.Cookie;


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserRepository userRepo) throws Exception {
        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
                .successHandler((request, response, authentication) -> {
                    OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
                    String username = oauthUser.getAttribute("login"); // GitHub username
                    Integer githubId = oauthUser.getAttribute("id"); // GitHub ID

                    // ✅ Save user if not exists
                    userRepo.findByUsername(username).orElseGet(() -> {
                        User user = new User();
                        user.setUsername(username);
                        user.setGithubId(githubId);
                        user.setProvider("github");
                        return userRepo.save(user);
                    });

                    // Generate JWT token
                    String token = JwtService.generateToken(username);
                    
                    // Set token in HTTP-only cookie
                    Cookie cookie = new Cookie("authToken", token);
                    cookie.setHttpOnly(true); 
                    cookie.setSecure(false);
                    cookie.setPath("/");
                    cookie.setMaxAge(24 * 60 * 60);

                    response.addCookie(cookie);
                    response.sendRedirect("http://localhost:4000/college-bound/");
                })
            )
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> {}));

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(JwtService.getKey()).build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4000") // Frontend URL
                        .allowedMethods("*")
                        .allowCredentials(true);
            }
        };
    }
}