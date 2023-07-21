package com.facts.financial_facts_service.services.facts;

import com.facts.financial_facts_service.handlers.FactsSyncHandler;
import com.facts.financial_facts_service.constants.TestConstants;
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

//    @Test
//    public void testGetFactsWithCikSuccess() {
//        mockFactsWebClientExchange();
//        FactsWrapper factsWrapper = new FactsWrapper();
//        Facts facts = new Facts(CIK, LocalDate.now(), factsWrapper);
//        FactsData response = factsService.getFactsWithCik(CIK).block();
//        verify(factsWebClient, times(1)).get();
//        assertEquals(facts, response);
//    }

    @Test
    public void testGetFactsWithCikFailure() {
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
            factsService.getFactsWithCik(CIK).block();
        });
    }

    @Test
    public void testGetFactsWithCikNotFound() {
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
            factsService.getFactsWithCik(CIK).block();
        });
    }

//    @Test
//    public void testGetFactsWithCikFromDBUpToDate() {
//        FactsWrapper factsWrapper = new FactsWrapper();
//        Facts facts = new Facts(CIK, LocalDate.now(), factsWrapper);
//        when(factsRepository.findById(CIK)).thenReturn(Optional.of(facts));
//        FactsData response = factsService.getFactsWithCik(CIK).block();
//        assertEquals(CIK, response.cik());
//    }

//    @Test
//    public void testGetFactsWithCikFromDBOutdated() {
//        mockFactsWebClientExchange();
//        Facts facts = new Facts(CIK, LocalDate.now().minusDays(7), FACTS);
//        when(factsRepository.findById(CIK)).thenReturn(Optional.of(facts));
//        FactsData response = factsService.getFactsWithCik(CIK).block();
//        verify(factsWebClient, times(1)).get();
//        assert(response.getLastSync()
//                .isAfter((LocalDate.now().minusDays(7))));
//    }

    @Test
    public void testGetFactsWithCikFromDBFailure() {
        DataAccessException ex = mock(DataAccessException.class);
        when(factsRepository.findById(CIK)).thenThrow(ex);
        assertThrows(ResponseStatusException.class, () -> {
            factsService.getFactsWithCik(CIK).block();
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
