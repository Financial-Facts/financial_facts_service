package com.facts.financial_facts_service.configurations;

import com.facts.financial_facts_service.components.BasicAuthEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired private BasicAuthEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf()
            .disable()
            .addFilterBefore(rateLimitFilter(), BasicAuthenticationFilter.class)
            .authorizeHttpRequests()
            .requestMatchers(new RegexRequestMatcher(
                    "/v1/discount/[Cc][Ii][Kk]\\d{10}",
                    HttpMethod.GET.name()))
            .permitAll()
            .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/swagger-config",
                    "/api-docs.yaml",
                    "/actuator/health",
                    "/v1/facts/*",
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
    public RateLimitFilter rateLimitFilter() {
        return new RateLimitFilter();
    }
}
