package com.facts.financial_facts_service.configurations.TaxonomyKeyMap.models;

import com.facts.financial_facts_service.entities.models.QuarterlyData;
import com.facts.financial_facts_service.services.facts.components.retriever.models.KeysContainer;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class TaxonomyKeyMap {

    private Map<Class<? extends QuarterlyData>, KeysContainer> gaap;

    private Map<Class<? extends QuarterlyData>, KeysContainer> ifrs;

}
