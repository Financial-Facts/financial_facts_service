package com.facts.financial_facts_service.entities.facts.models;

import com.facts.financial_facts_service.entities.facts.Facts;

public record FactsData(String cik, FactsWrapper facts) {

    public FactsDataResponse(Facts facts) {
        this(facts.getCik(), facts.getData());
    }

}
