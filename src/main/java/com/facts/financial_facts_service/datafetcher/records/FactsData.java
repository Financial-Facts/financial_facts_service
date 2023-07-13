package com.facts.financial_facts_service.datafetcher.records;

import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.entities.facts.models.FactsWrapper;

public record FactsData(String cik, FactsWrapper facts) {

    public FactsData(Facts facts) {
        this(facts.getCik(), facts.getData());
    }

}
