package com.facts.financial_facts_service.services.facts.components.retriever.models;

import java.util.List;

public record KeysContainer (List<String> gaapKeys,
                             List<String> ifrsKeys,
                             List<String> deiKeys) {
}