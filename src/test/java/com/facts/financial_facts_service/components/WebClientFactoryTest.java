package com.facts.financial_facts_service.components;

import com.facts.financial_facts_service.constants.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebClientFactoryTest implements TestConstants {

    @InjectMocks
    private WebClientFactory webClientFactory;

    @Mock
    private Logger mockLogger;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        webClientFactory = new WebClientFactory();
        webClientFactory.logger = mockLogger;
    }

    @Test
    public void testBuildWebClientFactoryWithoutHeaders() {
        WebClient webClient = webClientFactory.buildWebClient(SEC_URL, Optional.empty());
        verify(mockLogger).info("Initializing webclient for url {}...", SEC_URL);
        assertNotNull(webClient);
    }

    @Test
    public void testBuildWebClientFactoryWithHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.USER_AGENT, TestConstants.USER_AGENT);
        WebClient webClient = webClientFactory.buildWebClient(SEC_URL, Optional.of(headers));
        assertNotNull(webClient);
        verify(mockLogger).info("Initializing webclient for url {}...", SEC_URL);
    }
}
