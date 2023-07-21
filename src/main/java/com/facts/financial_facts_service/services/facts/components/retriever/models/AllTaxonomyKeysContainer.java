package com.facts.financial_facts_service.services.facts.components.retriever.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AllTaxonomyKeysContainer {

    List<String> gaapKeys;
    List<String> ifrsKeys;
    List<String> deiKeys;

}
