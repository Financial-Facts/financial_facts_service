package com.facts.financial_facts_service.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import com.facts.financial_facts_service.constants.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;

import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.entities.identity.IdentityRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class IdentityMapTest {

    @Mock
    private WebClient secWebClient;

    @Mock
    private IdentityRepository identityRepository;

    @InjectMocks
    private IdentityMap mockIdentityMap;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetValueWithValidCik() throws InterruptedException {
        Identity identity = Identity.builder()
            .cik(TestConstants.CIK)
            .symbol(TestConstants.SYMBOL)
            .name(TestConstants.NAME).build();
        mockIdentityMap.setValue(TestConstants.CIK, identity);
        Mono<Optional<Identity>> resultMono = mockIdentityMap.getValue(TestConstants.CIK);
        StepVerifier.create(resultMono)
                .expectNext(Optional.of(identity))
                .verifyComplete();
    }

    @Test
    public void testGetValueWithInvalidCik() throws InterruptedException {
        Mono<Optional<Identity>> resultMono = mockIdentityMap.getValue(TestConstants.CIK);
        StepVerifier.create(resultMono)
                .expectNext(Optional.empty())
                .verifyComplete();
    }

    @Test
    public void testGetFromDBIfIdentityTablePopulated() {
        when(identityRepository.count()).thenReturn(1L);
        ArrayList<Identity> identities = new ArrayList<>();
        Identity identity = new Identity();
        identity.setCik(TestConstants.CIK);
        identities.add(identity);
        when(identityRepository.findAll()).thenReturn(identities);
        mockIdentityMap.run();
        verify(identityRepository, times(1)).findAll();
    }

    @Test
    public void testGetFromSECIfDBNotPopulated() {
        when(identityRepository.count()).thenReturn(0L);
        WebClient.RequestHeadersUriSpec mockHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        when(secWebClient.get()).thenReturn(mockHeadersUriSpec);
        HashMap<String, Identity> identityHashMap = new HashMap<>();
        when(mockHeadersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(identityHashMap));
        mockIdentityMap.run();
        verify(secWebClient, times(1)).get();
    }

    @Test
    public void testPopulateIdentityMapOnRun() {
        when(identityRepository.count()).thenReturn(0L);
        WebClient.RequestHeadersUriSpec mockHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        when(secWebClient.get()).thenReturn(mockHeadersUriSpec);
        HashMap<String, Identity> identityHashMap = new HashMap<>();
        Identity identity = new Identity();
        identity.setCik(TestConstants.CIK);
        identityHashMap.put(TestConstants.CIK, identity);
        when(mockHeadersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(identityHashMap));
        mockIdentityMap.run();
        assertNotNull(mockIdentityMap.getValue(TestConstants.CIK));
        verify(identityRepository, times(1)).save(Mockito.eq(identity));
    }

}
