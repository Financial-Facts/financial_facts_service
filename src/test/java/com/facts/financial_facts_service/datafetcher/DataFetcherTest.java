package com.facts.financial_facts_service.datafetcher;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.datafetcher.records.FactsData;
import com.facts.financial_facts_service.datafetcher.records.StickerPriceData;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.services.facts.FactsService;
import com.facts.financial_facts_service.services.identity.IdentityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataFetcherTest implements TestConstants {

    @Mock
    private IdentityService identityService;

    @Mock
    private FactsService factsService;

    @InjectMocks
    private DataFetcher dataFetcher;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetFactsWithCik() {
        Facts facts = new Facts();
        facts.setCik(CIK);
        when(factsService.getFactsWithCik(CIK)).thenReturn(Mono.just(facts));
        FactsData actual = dataFetcher.getFactsWithCik(CIK).block();
        verify(factsService, times(1)).getFactsWithCik(CIK);
        assertNotNull(actual);
        assertEquals(CIK, actual.cik());
    }

    @Test
    public void testGetStickerPriceDataWithCik() {
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
