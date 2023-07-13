package com.facts.financial_facts_service.services.facts.components;

import com.amazonaws.services.simplesystemsmanagement.model.FeatureNotAvailableException;
import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.ModelType;
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
@NoArgsConstructor
public class RetrieverFactory implements Constants {

    @Autowired
    private GaapRetriever gaapRetriever;

    @Autowired
    private IfrsRetriever ifrsRetriever;

    public IRetriever getRetriever(String cik, FactsWrapper factsJson) {
        if (Objects.isNull(factsJson.getTaxonomyReports())) {
            throw new DataNotFoundException(ModelType.FACTS, cik);
        }
        if (Objects.nonNull(factsJson.getTaxonomyReports().getGaap())) {
            return gaapRetriever;
        }
        if (Objects.nonNull(factsJson.getTaxonomyReports().getIfrs())) {
            throw new FeatureNotImplementedException("IFRS taxonomy parsing not currently supported");
        }
        throw new DataNotFoundException(ModelType.FACTS, cik);
    }
}
