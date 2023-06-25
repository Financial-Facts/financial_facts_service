package com.facts.financial_facts_service.components;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.ModelType;
import com.facts.financial_facts_service.entities.facts.parser.models.FactsResponseWrapper;
import com.facts.financial_facts_service.entities.facts.retriever.GaapRetriever;
import com.facts.financial_facts_service.entities.facts.retriever.IRetriever;
import com.facts.financial_facts_service.entities.facts.retriever.IfrsRetriever;
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

    public IRetriever getRetriever(String cik, FactsResponseWrapper factsJson) {
        if (Objects.nonNull(factsJson.getTaxonomyReports().getGaap())) {
            return gaapRetriever;
        }
        if (Objects.nonNull(factsJson.getTaxonomyReports().getIfrs())) {
            return ifrsRetriever;
        }
        throw new DataNotFoundException(ModelType.FACTS, cik);
    }
}
