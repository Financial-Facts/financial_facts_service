package com.facts.financial_facts_service.services.facts.components.retriever;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.Taxonomy;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
public class GaapRetriever extends AbstractRetriever implements IRetriever, Constants {

    @PostConstruct
    private void init() {
        taxonomy = Taxonomy.US_GAAP;
        keysMap = factsKeysManager.getKeysMapForTaxonomy(Taxonomy.US_GAAP);
    }
}
