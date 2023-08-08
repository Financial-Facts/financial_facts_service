package com.facts.financial_facts_service.services.facts.components;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.facts.models.FactsWrapper;
import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.facts.models.UnitData;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.services.facts.components.retriever.GaapRetriever;
import com.facts.financial_facts_service.services.facts.components.retriever.IfrsRetriever;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class RetrieverSelectorTest implements TestConstants {

    @Mock
    private GaapRetriever gaapRetriever;

    @Mock
    private IfrsRetriever ifrsRetriever;

    @InjectMocks
    private RetrieverSelector retrieverSelector;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRetrieverNullReports() {
        FactsWrapper factsWrapper = new FactsWrapper();
        assertThrows(DataNotFoundException.class, () ->
                retrieverSelector.getRetriever(CIK, factsWrapper));
    }

    @Test
    public void testGetRetrieverUnknownReports() {
        FactsWrapper factsWrapper = new FactsWrapper();
        TaxonomyReports taxonomyReports = new TaxonomyReports();
        factsWrapper.setTaxonomyReports(taxonomyReports);
        assertThrows(DataNotFoundException.class, () ->
                retrieverSelector.getRetriever(CIK, factsWrapper));
    }

    @Test
    public void testGetRetrieverGaap() {
        FactsWrapper factsWrapper = new FactsWrapper();
        TaxonomyReports taxonomyReports = new TaxonomyReports();
        Map<String, UnitData> gaap = new HashMap<>();
        taxonomyReports.setGaap(gaap);
        factsWrapper.setTaxonomyReports(taxonomyReports);
        assertEquals(gaapRetriever, retrieverSelector.getRetriever(CIK, factsWrapper));
    }

    @Test
    public void testGetRetrieverIfrs() {
        FactsWrapper factsWrapper = new FactsWrapper();
        TaxonomyReports taxonomyReports = new TaxonomyReports();
        Map<String, UnitData> ifrs = new HashMap<>();
        taxonomyReports.setIfrs(ifrs);
        factsWrapper.setTaxonomyReports(taxonomyReports);
        assertEquals(ifrsRetriever, retrieverSelector.getRetriever(CIK, factsWrapper));
    }
    @Test
    public void testGetRetrieverPrioritizeGaap() {
        FactsWrapper factsWrapper = new FactsWrapper();
        TaxonomyReports taxonomyReports = new TaxonomyReports();
        Map<String, UnitData> report = new HashMap<>();
        taxonomyReports.setGaap(report);
        taxonomyReports.setIfrs(report);
        factsWrapper.setTaxonomyReports(taxonomyReports);
        assertEquals(gaapRetriever, retrieverSelector.getRetriever(CIK, factsWrapper));
    }

}
