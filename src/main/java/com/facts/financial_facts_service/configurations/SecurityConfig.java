package com.facts.financial_facts_service.configurations;

import com.facts.financial_facts_service.components.BasicAuthEntryPoint;
import com.facts.financial_facts_service.components.RateLimitFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired private BasicAuthEntryPoint authenticationEntryPoint;

    @Autowired private RateLimitFilter rateLimitFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors()
            .and()
            .addFilterAfter(rateLimitFilter, BasicAuthenticationFilter.class)
            .csrf()
            .disable()
            .authorizeHttpRequests()
            .requestMatchers(new RegexRequestMatcher(
                        "/v1/discount/[Cc][Ii][Kk]\\d{10}",
                        HttpMethod.GET.name()),
                    new RegexRequestMatcher(
                        "/v1/facts/[Cc][Ii][Kk]\\d{10}",
                        HttpMethod.GET.name()))
            .permitAll()
            .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/swagger-config",
                    "/api-docs.yaml",
                    "/actuator/health",
                    "/v1/identity/bulk")
            .permitAll()
            .and()
            .authorizeHttpRequests()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic()
            .authenticationEntryPoint(authenticationEntryPoint);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
