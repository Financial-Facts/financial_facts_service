package com.facts.financial_facts_service.configurations;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.identity.Identity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IdentityMapConfigTest implements TestConstants {

    @Mock
    private WebClient secWebClient;

    @InjectMocks
    private IdentityMapConfig identityMapConfig;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPopulateIdentityMapOnRun() {
        WebClient.RequestHeadersUriSpec mockHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        when(secWebClient.get()).thenReturn(mockHeadersUriSpec);
        ConcurrentHashMap<String, Identity> identityHashMap = new ConcurrentHashMap<>();
        Identity identity = new Identity();
        identity.setCik(CIK);
        identityHashMap.put(CIK, identity);
        when(mockHeadersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(identityHashMap));
        ConcurrentHashMap<String, Identity> actual = identityMapConfig.identityMap();
        assertNotNull(actual);
        assertNotNull(actual.get(CIK));
        assertEquals(CIK, actual.get(CIK).getCik());
    }
}
