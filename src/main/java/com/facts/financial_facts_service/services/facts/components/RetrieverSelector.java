package com.facts.financial_facts_service.services.facts.components;

import com.amazonaws.services.simplesystemsmanagement.model.FeatureNotAvailableException;
import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.ModelType;
import com.facts.financial_facts_service.constants.Taxonomy;
import com.facts.financial_facts_service.entities.facts.models.FactsWrapper;
import com.facts.financial_facts_service.exceptions.FeatureNotImplementedException;
import com.facts.financial_facts_service.services.facts.components.retriever.GaapRetriever;
import com.facts.financial_facts_service.services.facts.components.retriever.IRetriever;
import com.facts.financial_facts_service.services.facts.components.retriever.IfrsRetriever;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RetrieverSelector implements Constants {

    @Autowired
    private GaapRetriever gaapRetriever;

    @Autowired
    private IfrsRetriever ifrsRetriever;

    public IRetriever getRetriever(String cik, FactsWrapper factsWrapper) {
        if (Objects.isNull(factsWrapper.getTaxonomyReports())) {
            throw new DataNotFoundException(ModelType.FACTS, cik);
        }
        if (Objects.nonNull(factsWrapper.getTaxonomyReports().getGaap())) {
            factsWrapper.getTaxonomyReports().setPrimaryTaxonomy(Taxonomy.US_GAAP);
            return gaapRetriever;
        }
        if (Objects.nonNull(factsWrapper.getTaxonomyReports().getIfrs())) {
            factsWrapper.getTaxonomyReports().setPrimaryTaxonomy(Taxonomy.IFRS_FULL);
            return ifrsRetriever;
        }
        throw new DataNotFoundException(ModelType.FACTS, cik);
    }
}
