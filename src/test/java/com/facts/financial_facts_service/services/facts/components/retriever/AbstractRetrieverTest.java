package com.facts.financial_facts_service.services.facts.components.retriever;

import com.facts.financial_facts_service.constants.Taxonomy;
import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyShareholderEquity;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import com.facts.financial_facts_service.services.facts.components.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class AbstractRetrieverTest implements TestConstants {

    @Mock
    private Parser parser;

    @InjectMocks
    private GaapRetriever abstractRetriever;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFetchQuarterlyDataShareholderEquity() {
        TaxonomyReports taxonomyReports = new TaxonomyReports();

        List<?> actual = abstractRetriever.retrieveStickerPriceData(CIK, taxonomyReports).block();
        verify(parser).retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP, List.of("StockholdersEquity",
                        "LiabilitiesAndStockholdersEquity"),
                Collections.emptyList(), QuarterlyShareholderEquity.class);
        assertEquals(1, actual.size());
        assertEquals(CIK, actual.get(0));
    }

    private  <T extends AbstractQuarterlyData> void mockRetrieveQuarterlyDataCall(TaxonomyReports taxonomyReports, Class<T> type) {
        T quarterlyData = buildQuarterlyData(type);
        when(parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP, List.of("StockholdersEquity",
                        "LiabilitiesAndStockholdersEquity"),
                Collections.emptyList(), type)).thenReturn(Mono.just(List.of(quarterlyData)));
    }

    private <T extends AbstractQuarterlyData> T buildQuarterlyData(Class<T> type) {
        try {
            T result = type.getDeclaredConstructor().newInstance();
            result.setCik(CIK);
            return result;
        } catch (InstantiationException |
                 IllegalAccessException |
                 InvocationTargetException |
                 NoSuchMethodException e) {
            return null;
        }
    }

}
