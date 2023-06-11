package com.facts.financial_facts_service.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.facts.financial_facts_service.constants.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.facts.financial_facts_service.entities.identity.Identity;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IdentityMapTest implements TestConstants {

    @Mock
    private WebClient secWebClient;

    @Mock
    private WebClientFactory webClientFactory;

    @InjectMocks
    private IdentityMap mockIdentityMap;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(mockIdentityMap, "secEndpoint", SEC_URL);
        ReflectionTestUtils.setField(mockIdentityMap, "userAgent", USER_AGENT);
        ReflectionTestUtils.setField(mockIdentityMap, "webClientFactory", webClientFactory);
    }

    @Test
    public void testGetValueWithValidCik() throws InterruptedException {
        Identity identity = Identity.builder()
            .cik(CIK)
            .symbol(SYMBOL)
            .name(NAME).build();
        mockIdentityMap.setValue(CIK, identity);
        Mono<Optional<Identity>> resultMono = mockIdentityMap.getValue(CIK);
        StepVerifier.create(resultMono)
                .expectNext(Optional.of(identity))
                .verifyComplete();
    }

    @Test
    public void testGetValueWithInvalidCik() throws InterruptedException {
        Mono<Optional<Identity>> resultMono = mockIdentityMap.getValue(CIK);
        StepVerifier.create(resultMono)
                .expectNext(Optional.empty())
                .verifyComplete();
    }

    @Test
    public void testGetFromSECIfDBNotPopulated() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.USER_AGENT, USER_AGENT);
        when(webClientFactory.buildWebClient(SEC_URL, Optional.of(headers)))
            .thenReturn(this.secWebClient);
        WebClient.RequestHeadersUriSpec mockHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        when(secWebClient.get()).thenReturn(mockHeadersUriSpec);
        HashMap<String, Identity> identityHashMap = new HashMap<>();
        when(mockHeadersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(identityHashMap));
        mockIdentityMap.run();
        verify(secWebClient, times(1)).get();
    }

    @Test
    public void testPopulateIdentityMapOnRun() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.USER_AGENT, USER_AGENT);
        when(webClientFactory.buildWebClient(SEC_URL, Optional.of(headers)))
                .thenReturn(this.secWebClient);
        WebClient.RequestHeadersUriSpec mockHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        when(secWebClient.get()).thenReturn(mockHeadersUriSpec);
        HashMap<String, Identity> identityHashMap = new HashMap<>();
        Identity identity = new Identity();
        identity.setCik(CIK);
        identityHashMap.put(CIK, identity);
        when(mockHeadersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(identityHashMap));
        mockIdentityMap.run();
        assertNotNull(mockIdentityMap.getValue(CIK));
    }

}
