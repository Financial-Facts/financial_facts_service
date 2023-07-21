package com.facts.financial_facts_service.configurations;

import com.facts.financial_facts_service.components.BasicAuthEntryPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @Mock
    private BasicAuthEntryPoint mockAuthenticationEntryPoint;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        securityConfig = new SecurityConfig();
        ReflectionTestUtils.setField(securityConfig, "authenticationEntryPoint", mockAuthenticationEntryPoint);
    }
}
