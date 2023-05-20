package com.facts.financial_facts_service.services;

import com.facts.financial_facts_service.components.FactsSyncHandler;
import com.facts.financial_facts_service.components.WebClientFactory;
import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.repositories.FactsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FactsServiceTest implements TestConstants {

    @Mock
    private WebClientFactory webClientFactory;

    @Mock
    private WebClient factsWebClient;

    @Mock
    private FactsRepository factsRepository;

    @Mock
    private FactsSyncHandler factsSyncHandler;

    @InjectMocks
    private FactsService factsService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(factsService, "factsWebClient", factsWebClient);
        ReflectionTestUtils.setField(factsService, "factsRepository", factsRepository);
        ReflectionTestUtils.setField(factsService, "webClientFactory", webClientFactory);
        ReflectionTestUtils.setField(factsService, "factsSyncHandler", factsSyncHandler);
        ReflectionTestUtils.setField(factsService, "factsGatewayUrl", FACTS_URL);
    }

    @Test
    public void testGetFactsByCikSuccess() {
        mockFactsWebClientExchange();
        Facts facts = new Facts(CIK, LocalDate.now(), FACTS);
        ResponseEntity<Facts> response = factsService.getFactsByCik(CIK).block();
        verify(factsWebClient, times(1)).get();
        assertEquals(facts, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetFactsByCikFailure() {
        WebClientResponseException ex = mock(WebClientResponseException.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        WebClient.RequestHeadersUriSpec headersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        when(factsWebClient.get()).thenReturn(headersUriSpec);
        when(headersUriSpec.uri(Mockito.anyString()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class))
                .thenThrow(ex);
        assertThrows(ResponseStatusException.class, () -> {
            factsService.getFactsByCik(CIK).block();
        });
    }

    @Test
    public void testGetFactsByCikNotFound() {
        WebClientResponseException ex = mock(WebClientResponseException.NotFound.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        WebClient.RequestHeadersUriSpec headersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        when(factsWebClient.get()).thenReturn(headersUriSpec);
        when(headersUriSpec.uri(Mockito.anyString()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class))
                .thenThrow(ex);
        assertThrows(DataNotFoundException.class, () -> {
            factsService.getFactsByCik(CIK).block();
        });
    }

    @Test
    public void testGetFactsByCikFromDBUpToDate() {
        Facts facts = new Facts(CIK, LocalDate.now(), FACTS);
        when(factsRepository.findById(CIK)).thenReturn(Optional.of(facts));
        ResponseEntity<Facts> response = factsService.getFactsByCik(CIK).block();
        assertEquals(CIK, response.getBody().getCik());
        assertEquals(FACTS, response.getBody().getData());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetFactsByCikFromDBOutdated() {
        mockFactsWebClientExchange();
        Facts facts = new Facts(CIK, LocalDate.now().minusDays(7), FACTS);
        when(factsRepository.findById(CIK)).thenReturn(Optional.of(facts));
        ResponseEntity<Facts> response = factsService.getFactsByCik(CIK).block();
        verify(factsWebClient, times(1)).get();
        assert(response.getBody().getLastSync()
                .isAfter((LocalDate.now().minusDays(7))));
    }

    @Test
    public void testGetFactsByCikFromDBFailure() {
        DataAccessException ex = mock(DataAccessException.class);
        when(factsRepository.findById(CIK)).thenThrow(ex);
        assertThrows(ResponseStatusException.class, () -> {
            factsService.getFactsByCik(CIK).block();
        });
    }

    private void mockFactsWebClientExchange() {
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        WebClient.RequestHeadersUriSpec headersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        when(factsWebClient.get()).thenReturn(headersUriSpec);
        when(headersUriSpec.uri(Mockito.anyString()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class))
                .thenReturn(Mono.just(new ResponseEntity<>(FACTS, HttpStatus.OK)));
    }
}
