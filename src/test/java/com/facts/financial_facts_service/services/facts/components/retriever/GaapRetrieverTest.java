package com.facts.financial_facts_service.services.facts.components.retriever;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.constants.enums.Taxonomy;
import com.facts.financial_facts_service.constants.interfaces.FactKeys;
import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.models.QuarterlyData;
import com.facts.financial_facts_service.services.facts.components.retriever.components.Parser;
import com.facts.financial_facts_service.services.facts.components.retriever.models.StickerPriceQuarterlyData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GaapRetrieverTest implements TestConstants, FactKeys {

    @Mock
    private Parser parser;

    @InjectMocks
    private GaapRetriever gaapRetriever;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(gaapRetriever, "parser", parser);
        gaapRetriever.init();
    }

    @Test
    public void testRetrieveStickerPriceDataSuccess() {
        TaxonomyReports taxonomyReports = new TaxonomyReports();
        taxonomyReports.setPrimaryTaxonomy(Taxonomy.US_GAAP);
        mockParseReportsForQuarterlyData(taxonomyReports);
        StickerPriceQuarterlyData actual = gaapRetriever.retrieveStickerPriceData(CIK, taxonomyReports).block();
        assertNotNull(actual);

        verify(parser).parseReportsForQuarterlyData(
                CIK, taxonomyReports, SHAREHOLDER_EQUITY_KEYS.gaapKeys(), SHAREHOLDER_EQUITY_KEYS.deiKeys());
        assertNotNull(actual.getQuarterlyShareholderEquity());
        assertEquals(1, actual.getQuarterlyShareholderEquity().size());

        verify(parser).parseReportsForQuarterlyData(
                CIK, taxonomyReports, OUTSTANDING_SHARES_KEYS.gaapKeys(), OUTSTANDING_SHARES_KEYS.deiKeys());
        assertNotNull(actual.getQuarterlyOutstandingShares());
        assertEquals(1, actual.getQuarterlyOutstandingShares().size());

        verify(parser).parseReportsForQuarterlyData(
                CIK, taxonomyReports, EARNINGS_PER_SHARE_KEYS.gaapKeys(), EARNINGS_PER_SHARE_KEYS.deiKeys());
        assertNotNull(actual.getQuarterlyFactsEPS());
        assertEquals(1, actual.getQuarterlyFactsEPS().size());

        verify(parser).parseReportsForQuarterlyData(
                CIK, taxonomyReports, LONG_TERM_DEBT_KEYS.gaapKeys(), LONG_TERM_DEBT_KEYS.deiKeys());
        assertNotNull(actual.getQuarterlyLongTermDebt());
        assertEquals(1, actual.getQuarterlyLongTermDebt().size());

        verify(parser).parseReportsForQuarterlyData(
                CIK, taxonomyReports, NET_INCOME_KEYS.gaapKeys(), NET_INCOME_KEYS.deiKeys());
        assertNotNull(actual.getQuarterlyNetIncome());
        assertEquals(1, actual.getQuarterlyNetIncome().size());
    }

    private void mockParseReportsForQuarterlyData(TaxonomyReports taxonomyReports) {
        QuarterlyData quarterlyData = new QuarterlyData();
        quarterlyData.setCik(CIK);
        quarterlyData.setAnnouncedDate(LocalDate.now());
        quarterlyData.setValue(BigDecimal.valueOf(1));
        when(parser.parseReportsForQuarterlyData(CIK, taxonomyReports, SHAREHOLDER_EQUITY_KEYS.gaapKeys(), SHAREHOLDER_EQUITY_KEYS.deiKeys()))
                .thenReturn(Mono.just(List.of(quarterlyData)));
        when(parser.parseReportsForQuarterlyData(CIK, taxonomyReports, OUTSTANDING_SHARES_KEYS.gaapKeys(), OUTSTANDING_SHARES_KEYS.deiKeys()))
                .thenReturn(Mono.just(List.of(quarterlyData)));
        when(parser.parseReportsForQuarterlyData(CIK, taxonomyReports, EARNINGS_PER_SHARE_KEYS.gaapKeys(), EARNINGS_PER_SHARE_KEYS.deiKeys()))
                .thenReturn(Mono.just(List.of(quarterlyData)));
        when(parser.parseReportsForQuarterlyData(CIK, taxonomyReports, LONG_TERM_DEBT_KEYS.gaapKeys(), LONG_TERM_DEBT_KEYS.deiKeys()))
                .thenReturn(Mono.just(List.of(quarterlyData)));
        when(parser.parseReportsForQuarterlyData(CIK, taxonomyReports, NET_INCOME_KEYS.gaapKeys(), NET_INCOME_KEYS.deiKeys()))
                .thenReturn(Mono.just(List.of(quarterlyData)));
    }

}
