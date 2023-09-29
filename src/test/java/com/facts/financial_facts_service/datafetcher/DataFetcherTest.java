package com.facts.financial_facts_service.datafetcher;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.datafetcher.projections.SimpleDiscount;
import com.facts.financial_facts_service.datafetcher.records.IdentitiesAndDiscounts;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.entities.identity.models.BulkIdentitiesRequest;
import com.facts.financial_facts_service.services.DiscountService;
import com.facts.financial_facts_service.services.FactsService;
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
    private DiscountService discountService;

    @InjectMocks
    private DataFetcher dataFetcher;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
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
            verify(identityService).getBulkIdentities(request);
            verify(discountService).getBulkSimpleDiscounts(true);
            assertNotNull(actual);
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
            verify(identityService).getBulkIdentities(request);
            verify(discountService, times(0)).getBulkSimpleDiscounts(true);
            assertNotNull(actual);
            assertNotNull(actual.identities());
            assertEquals(1, actual.identities().size());
            assertEquals(CIK, actual.identities().get(0).getCik());
            assertNull(actual.discounts());
        }

    }
}
