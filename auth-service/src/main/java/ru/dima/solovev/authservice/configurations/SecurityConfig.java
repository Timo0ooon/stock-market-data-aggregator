package ru.dima.solovev.authservice.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ru.dima.solovev.authservice.services.UserService;

import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SecurityConfig {
    private final UserService userService;
    private final JwtRequestFilter jwtRequestFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(
                authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/authentication").permitAll()
                        .requestMatchers(HttpMethod.POST, "/registration").permitAll()
                        .anyRequest().authenticated()
        );
        http.exceptionHandling(
                exception -> exception.authenticationEntryPoint(
                        (request, response, ex) ->  {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");

                            String jsonResponse = objectMapper.writeValueAsString(Map.of(
                                    "error", "Authentication failed",
                                    "message", ex.getMessage(),
                                    "timestamp", System.currentTimeMillis()
                            ));
                            response.getWriter().write(jsonResponse);
                        }
                )
        );
        http.sessionManagement(
                session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userService);

        return daoAuthenticationProvider;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
