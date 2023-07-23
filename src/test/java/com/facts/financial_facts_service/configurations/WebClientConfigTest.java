package com.facts.financial_facts_service.configurations;

import com.facts.financial_facts_service.constants.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WebClientConfigTest implements TestConstants {

    @Mock
    private Logger mockLogger;

    @InjectMocks
    private WebClientConfig webClientConfig;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(webClientConfig, "logger", mockLogger);
        ReflectionTestUtils.setField(webClientConfig, "factsGatewayUrl", FACTS_URL);
        ReflectionTestUtils.setField(webClientConfig, "bucketName", BUCKET_NAME);
        ReflectionTestUtils.setField(webClientConfig, "secEndpoint", SEC_URL);
        ReflectionTestUtils.setField(webClientConfig, "userAgent", USER_AGENT);
    }

    @Test
    public void testBuildSecWebClient() {
        WebClient webClient = webClientConfig.secWebClient();
        verify(mockLogger).info("Initializing webclient for url {}...", SEC_URL);
        assertNotNull(webClient);
    }

    @Test
    public void testBuildGatewayWebClient() {
        String url = FACTS_URL + "/" + BUCKET_NAME;
        WebClient webClient = webClientConfig.gatewayWebClient();
        verify(mockLogger).info("Initializing webclient for url {}...", url);
        assertNotNull(webClient);
    }
}
