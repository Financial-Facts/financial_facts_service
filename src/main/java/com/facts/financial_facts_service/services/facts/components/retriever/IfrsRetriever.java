package com.facts.financial_facts_service.services.facts.components.retriever;

import com.facts.financial_facts_service.constants.Taxonomy;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
public class IfrsRetriever extends AbstractRetriever implements IRetriever {

    @PostConstruct
    private void init() {
        taxonomy = Taxonomy.IFRS_FULL;
        keysMap = factsKeysManager.getKeysMapForTaxonomy(Taxonomy.IFRS_FULL);
    }
}