package com.facts.financial_facts_service.services.facts;

import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.entities.facts.models.FactsWrapper;
import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyFactsEPS;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyNetIncome;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyShareholderEquity;
import com.facts.financial_facts_service.entities.models.QuarterlyData;
import com.facts.financial_facts_service.handlers.FactsSyncHandler;
import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.repositories.FactsRepository;
import com.facts.financial_facts_service.services.facts.components.RetrieverSelector;
import com.facts.financial_facts_service.services.facts.components.retriever.GaapRetriever;
import com.facts.financial_facts_service.services.facts.components.retriever.IRetriever;
import com.facts.financial_facts_service.services.facts.components.retriever.models.StickerPriceQuarterlyData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FactsServiceTest implements TestConstants {

    @Mock
    private WebClient gatewayWebClient;

    @Mock
    private FactsRepository factsRepository;

    @Mock
    private FactsSyncHandler factsSyncHandler;

    @Mock
    private RetrieverSelector retrieverSelector;

    @InjectMocks
    private FactsService factsService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("fetchUpToDateFacts")
    class fetchUpToDateTests {

        @Test
        public void testReturnFactsFromGatewayIfDbOutdated() {
            Facts facts = new Facts();
            facts.setCik(CIK);
            facts.setLastSync(LocalDate.now().minusDays(8));
            when(factsRepository.findById(CIK)).thenReturn(Optional.of(facts));
            try {
                factsService.getFactsWithCik(CIK).block();
            } catch (NullPointerException ex) {
                verify(gatewayWebClient).get();
            }
        }
    }
    @Nested
    @DisplayName("getFactsFromDB")
    class getFactsFromDbTests {

        @Test
        public void testReturnFactsFromDbIfPresentAndUpToDate() {
            Facts facts = new Facts();
            facts.setCik(CIK);
            facts.setLastSync(LocalDate.now());
            when(factsRepository.findById(CIK)).thenReturn(Optional.of(facts));
            Facts actual = factsService.getFactsWithCik(CIK).block();
            verify(factsRepository).findById(CIK);
            verify(gatewayWebClient, times(0)).get();
            assertNotNull(actual);
            assertEquals(CIK, actual.getCik());
        }

        @Test
        public void testGetFactsFromGatewayIfNotPresentInDb() {
            when(factsRepository.findById(CIK)).thenReturn(Optional.empty());
            try {
                factsService.getFactsWithCik(CIK);
            } catch (NullPointerException ex) {
                verify(gatewayWebClient).get();
            }
        }

        @Test
        public void testDataAccessError() {
            DataAccessException ex = mock(DataAccessException.class);
            when(factsRepository.findById(CIK)).thenThrow(ex);
            assertThrows(ResponseStatusException.class, () ->
                    factsService.getFactsWithCik(CIK));
        }
    }

    @Nested
    @DisplayName("getFactsFromAPIGateway")
    class getFactsFromAPIGatewayTests {

        private FactsWrapper factsWrapper;

        @BeforeEach
        public void init() {
            factsWrapper = new FactsWrapper();
            mockFactsWebClientExchange(factsWrapper);
        }
        @Test
        public void testBuildsFactsWithGatewayResponse() {
            when(retrieverSelector.getRetriever(CIK, factsWrapper))
                    .thenThrow(new RuntimeException("stop"));
            assertThrows(RuntimeException.class, () -> {
               factsService.getFactsWithCik(CIK).block();
            });
        }

        @Test
        public void testSavesBuiltFacts() {
            IRetriever retriever = mock(GaapRetriever.class);
            when(retrieverSelector.getRetriever(CIK, factsWrapper))
                    .thenReturn(retriever);
            TaxonomyReports reports = new TaxonomyReports();
            when(retriever.retrieveStickerPriceData(CIK, reports))
                    .thenReturn(Mono.just(StickerPriceQuarterlyData.builder().build()));
            when(factsSyncHandler.pushToHandler(any(Facts.class)))
                    .thenThrow(new RuntimeException("stop"));
            assertThrows(RuntimeException.class, () -> {
                factsService.getFactsWithCik(CIK).block();
            });
        }

    }

    @Nested
    @DisplayName("queryAPIGateway")
    class queryAPIGatewayTests {

        @BeforeEach
        public void init() {
            when(factsRepository.findById(CIK)).thenReturn(Optional.empty());
        }

        @Test
        public void testQueryApiGatewaySuccess() {
            FactsWrapper factsWrapper = new FactsWrapper();
            mockFactsWebClientExchange(factsWrapper);
            when(retrieverSelector.getRetriever(CIK, factsWrapper))
                .thenThrow(new DataNotFoundException("stop"));
            try {
                factsService.getFactsWithCik(CIK).block();
            } catch (DataNotFoundException ex) {
                verify(gatewayWebClient).get();
                verify(retrieverSelector).getRetriever(CIK, factsWrapper);
                assertEquals("stop", ex.getMessage());
            }
        }

        @Test
        public void testQueryApiGatewayFactsNotFound() {
            FactsWrapper factsWrapper = new FactsWrapper();
            factsWrapper.setCik(CIK);
            WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
            WebClient.RequestHeadersUriSpec headersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
            WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
            when(gatewayWebClient.get()).thenReturn(headersUriSpec);
            when(headersUriSpec.uri("/" + CIK + ".json"))
                    .thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.onStatus(any(), any()))
                    .thenThrow(new DataNotFoundException("Not found"));
            try {
                factsService.getFactsWithCik(CIK).block();
                fail();
            } catch (DataNotFoundException ex) {
                verify(gatewayWebClient).get();
            }
        }
    }

    @Nested
    @DisplayName("buildFactsWithGatewayResponse")
    public class buildFactsWithGatewayResponseTests {

        private FactsWrapper factsWrapper;

        @BeforeEach
        public void init() {
            factsWrapper = new FactsWrapper();
            TaxonomyReports taxonomyReports = new TaxonomyReports();
            factsWrapper.setTaxonomyReports(taxonomyReports);
            mockFactsWebClientExchange(factsWrapper);
        }

        @Test
        public void testBuildFactsWithRetriever() {
            IRetriever retriever = mock(GaapRetriever.class);
            when(retrieverSelector.getRetriever(CIK, factsWrapper))
                    .thenReturn(retriever);
            StickerPriceQuarterlyData data = StickerPriceQuarterlyData.builder()
                    .quarterlyShareholderEquity(buildQuarterlyDataTypeList(QuarterlyShareholderEquity.class))
                    .quarterlyOutstandingShares(buildQuarterlyDataTypeList(QuarterlyOutstandingShares.class))
                    .quarterlyFactsEPS(buildQuarterlyDataTypeList(QuarterlyFactsEPS.class))
                    .quarterlyLongTermDebt(buildQuarterlyDataTypeList(QuarterlyLongTermDebt.class))
                    .quarterlyNetIncome(buildQuarterlyDataTypeList(QuarterlyNetIncome.class)).build();
            when(retriever.retrieveStickerPriceData(CIK, factsWrapper.getTaxonomyReports()))
                    .thenReturn(Mono.just(data));
            Facts actual = factsService.getFactsWithCik(CIK).block();
            verify(retrieverSelector).getRetriever(CIK, factsWrapper);
            verify(factsSyncHandler).pushToHandler(any(Facts.class));

            // Facts data
            assertNotNull(actual);
            assertNotNull(actual.getCik());
            assertNotNull(actual.getLastSync());
            assertNotNull(actual.getData());
            assertEquals(CIK, actual.getCik());
            assertEquals(factsWrapper, actual.getData());

            // Sticker price quarterly data
            assertStickerPriceQuarterlyData(actual.getQuarterlyShareholderEquity());
            assertStickerPriceQuarterlyData(actual.getQuarterlyOutstandingShares());
            assertStickerPriceQuarterlyData(actual.getQuarterlyEPS());
            assertStickerPriceQuarterlyData(actual.getQuarterlyLongTermDebt());
            assertStickerPriceQuarterlyData(actual.getQuarterlyNetIncome());
        }

        private <T extends QuarterlyData> void assertStickerPriceQuarterlyData(List<T> quarterlyData) {
            assertNotNull(quarterlyData);
            assertEquals(1, quarterlyData.size());
            assertNotNull(quarterlyData.get(0));
            assertEquals(CIK, quarterlyData.get(0).getCik());
        }

        private <T extends QuarterlyData> List<T> buildQuarterlyDataTypeList(Class<T> type) {
            QuarterlyData quarterlyData = new QuarterlyData();
            quarterlyData.setCik(CIK);
            return (List<T>) List.of(quarterlyData);
        }
    }

    private void mockFactsWebClientExchange(FactsWrapper factsWrapper) {
        WebClientResponseException ex = mock(WebClientResponseException.NotFound.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        WebClient.RequestHeadersUriSpec headersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        when(gatewayWebClient.get()).thenReturn(headersUriSpec);
        when(headersUriSpec.uri("/" + CIK + ".json"))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(FactsWrapper.class))
                .thenReturn(Mono.just(factsWrapper));
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    }
}
