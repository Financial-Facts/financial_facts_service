package com.facts.financial_facts_service.configurations;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SecWebClientConfigTest {

    private SecWebClientConfig secWebClientConfig;

    @BeforeEach
    public void init() {
        secWebClientConfig = new SecWebClientConfig();
    }

    @Test
    void testSecWebClientNotNull() {
        assertNotNull(secWebClientConfig.secWebClient());
    }

}
