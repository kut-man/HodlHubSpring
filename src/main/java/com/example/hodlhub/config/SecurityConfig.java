package com.example.hodlhub.config;

import com.example.hodlhub.security.CustomAuthenticationFailureHandler;
import com.example.hodlhub.security.CustomAuthenticationSuccessHandler;
import com.example.hodlhub.util.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.PrintWriter;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final ObjectMapper objectMapper;

  public SecurityConfig(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors((cors) -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(
            request -> request.requestMatchers("/auth/**").permitAll().anyRequest().authenticated())
        .formLogin(
            httpSecurityFormLoginConfigurer ->
                httpSecurityFormLoginConfigurer
                    .loginPage("/login")
                    .successHandler(new CustomAuthenticationSuccessHandler(objectMapper))
                    .failureHandler(new CustomAuthenticationFailureHandler(objectMapper))
                    .permitAll())
        .logout(
            logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessHandler(
                        new HttpStatusReturningLogoutSuccessHandler()) // Use
                                                                       // HttpStatusReturningLogoutSuccessHandler
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID"))
        .exceptionHandling(
            exceptionHandlingConfigurer ->
                exceptionHandlingConfigurer
                    .authenticationEntryPoint(unauthorizedEntryPoint())
                    .accessDeniedHandler(accessDeniedHandler()));

    return http.build();
  }

  @Bean
  public AuthenticationEntryPoint unauthorizedEntryPoint() {
    return (request, response, authException) -> {
      response.setContentType("application/json");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

      ApiResponse<Void> apiResponse =
          new ApiResponse<>(
              HttpStatus.UNAUTHORIZED, authException.getMessage(), request.getRequestURI());

      PrintWriter out = response.getWriter();
      out.print(objectMapper.writeValueAsString(apiResponse));
      out.flush();
    };
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return (request, response, accessDeniedException) -> {
      response.setContentType("application/json");
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);

      ApiResponse<Void> apiResponse =
          new ApiResponse<>(
              HttpStatus.FORBIDDEN, accessDeniedException.getMessage(), request.getRequestURI());

      PrintWriter out = response.getWriter();
      out.print(objectMapper.writeValueAsString(apiResponse));
      out.flush();
    };
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("http://localhost:5173");
    configuration.setAllowedMethods(Arrays.asList("POST", "GET"));
    configuration.setAllowCredentials(true);
    configuration.addAllowedHeader("*");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public PasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
