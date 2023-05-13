package com.facts.financial_facts_service.services;

import com.facts.financial_facts_service.components.WebClientFactory;
import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.facts.Facts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FactsServiceTest implements TestConstants {

    @Mock
    private WebClientFactory webClientFactory;

    @Mock
    private WebClient factsWebClient;

    @InjectMocks
    private FactsService factsService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(factsService, "factsWebClient", factsWebClient);
        ReflectionTestUtils.setField(factsService, "webClientFactory", webClientFactory);
        ReflectionTestUtils.setField(factsService, "factsGatewayUrl", FACTS_URL);
    }

    @Test
    public void testGetFactsByCikSuccess() {
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        WebClient.RequestHeadersUriSpec headersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        when(factsWebClient.get()).thenReturn(headersUriSpec);
        when(headersUriSpec.uri(((Function<UriBuilder, URI>) Mockito.any())))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class))
                .thenReturn(Mono.just(new ResponseEntity<>(FACTS, HttpStatus.OK)));
        Facts facts = new Facts(CIK, FACTS);
        ResponseEntity<Facts> response = factsService.getFactsByCik(CIK).block();
        assertEquals(facts, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    public void testGetFactsByCikFailure() {
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        WebClient.RequestHeadersUriSpec headersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        when(factsWebClient.get()).thenReturn(headersUriSpec);
        when(headersUriSpec.uri(((Function<UriBuilder, URI>) Mockito.any())))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class))
                .thenReturn(Mono.just(new ResponseEntity<>(FACTS, HttpStatus.BAD_GATEWAY)));
        assertThrows(ResponseStatusException.class, () -> {
            factsService.getFactsByCik(CIK).block();
        });
    }

}
