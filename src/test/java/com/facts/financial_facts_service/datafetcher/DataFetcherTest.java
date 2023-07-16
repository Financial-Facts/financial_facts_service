package com.facts.financial_facts_service.datafetcher;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.datafetcher.projections.SimpleDiscount;
import com.facts.financial_facts_service.datafetcher.records.FactsData;
import com.facts.financial_facts_service.datafetcher.records.IdentitiesAndDiscounts;
import com.facts.financial_facts_service.datafetcher.records.StickerPriceData;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.entities.identity.models.BulkIdentitiesRequest;
import com.facts.financial_facts_service.services.DiscountService;
import com.facts.financial_facts_service.services.facts.FactsService;
import com.facts.financial_facts_service.services.identity.IdentityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataFetcherTest implements TestConstants {

    @Mock
    private IdentityService identityService;

    @Mock
    private FactsService factsService;

    @Mock
    private DiscountService discountService;

    @InjectMocks
    private DataFetcher dataFetcher;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("getFactsWithCik")
    class getFactsWithCikTests {

        @Test
        public void testGetFactsWithCikSuccess() {
            Facts facts = new Facts();
            facts.setCik(CIK);
            when(factsService.getFactsWithCik(CIK)).thenReturn(Mono.just(facts));
            FactsData actual = dataFetcher.getFactsWithCik(CIK).block();
            verify(factsService, times(1)).getFactsWithCik(CIK);
            assertNotNull(actual);
            assertEquals(CIK, actual.cik());
        }
    }

    @Nested
    @DisplayName("getStickerPriceDataWithCik")
    class getStickerPriceDataWithCikTests {

        @Test
        public void testGetStickerPriceDataWithCikSuccess() {
            Facts facts = new Facts();
            facts.setCik(CIK);
            Identity identity = new Identity();
            identity.setCik(CIK);
            identity.setName(NAME);
            identity.setSymbol(SYMBOL);
            when(factsService.getFactsWithCik(CIK)).thenReturn(Mono.just(facts));
            when(identityService.getIdentityFromIdentityMap(CIK)).thenReturn(Mono.just(identity));
            StickerPriceData actual = dataFetcher.getStickerPriceDataWithCik(CIK).block();
            verify(factsService, times(1)).getFactsWithCik(CIK);
            verify(identityService, times(1)).getIdentityFromIdentityMap(CIK);
            assertNotNull(actual);
            assertEquals(CIK, actual.cik());
            assertEquals(NAME, actual.name());
            assertEquals(SYMBOL, actual.symbol());
        }
    }

    @Nested
    @DisplayName("getIdentitiesAndDiscounts")
    class getIdentitiesAndDiscountsTests {

        @Test
        public void testGetIdentitiesAndDiscounts() {
            BulkIdentitiesRequest request = new BulkIdentitiesRequest();
            Identity identity = new Identity();
            identity.setCik(CIK);
            List<Identity> identityList = List.of(identity);
            when(identityService.getBulkIdentities(request)).thenReturn(Mono.just(identityList));
            SimpleDiscount simpleDiscount = mock(SimpleDiscount.class);
            when(discountService.getBulkSimpleDiscounts(true))
                    .thenReturn(Mono.just(List.of(simpleDiscount)));
            IdentitiesAndDiscounts actual = dataFetcher.getIdentitiesAndOptionalDiscounts(request, true).block();
            verify(identityService, times(1)).getBulkIdentities(request);
            verify(discountService, times(1)).getBulkSimpleDiscounts(true);
            assertNotNull(actual.identities());
            assertEquals(1, actual.identities().size());
            assertEquals(CIK, actual.identities().get(0).getCik());
            assertNotNull(actual.discounts());
            assertEquals(1, actual.discounts().size());
            assertEquals(simpleDiscount, actual.discounts().get(0));
        }

        @Test
        public void testGetIdentitiesAndDiscountsWithFalse() {
            BulkIdentitiesRequest request = new BulkIdentitiesRequest();
            Identity identity = new Identity();
            identity.setCik(CIK);
            List<Identity> identityList = List.of(identity);
            when(identityService.getBulkIdentities(request)).thenReturn(Mono.just(identityList));
            IdentitiesAndDiscounts actual = dataFetcher.getIdentitiesAndOptionalDiscounts(request, false).block();
            verify(identityService, times(1)).getBulkIdentities(request);
            verify(discountService, times(0)).getBulkSimpleDiscounts(true);
            assertNotNull(actual.identities());
            assertEquals(1, actual.identities().size());
            assertEquals(CIK, actual.identities().get(0).getCik());
            assertNull(actual.discounts());
        }

    }
}
