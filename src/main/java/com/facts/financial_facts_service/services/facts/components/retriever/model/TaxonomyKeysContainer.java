package com.facts.financial_facts_service.services.facts.components.retriever.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TaxonomyKeysContainer {

    List<String> gaapKeys;
    List<String> ifrsKeys;
    List<String> deiKeys;

}
