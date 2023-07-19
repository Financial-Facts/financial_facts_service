package com.facts.financial_facts_service.services.facts.components.retriever;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.Taxonomy;
import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyFactsEPS;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyNetIncome;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyShareholderEquity;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import com.facts.financial_facts_service.services.facts.components.parser.Parser;
import com.facts.financial_facts_service.services.facts.components.retriever.components.FactsKeysManager;
import com.facts.financial_facts_service.services.facts.components.retriever.model.KeysContainer;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


public abstract class AbstractRetriever implements IRetriever, Constants {

    @Autowired
    private Parser parser;

    @Autowired
    protected FactsKeysManager factsKeysManager;

    protected Map<Class<? extends AbstractQuarterlyData>, KeysContainer> keysMap;

    protected Taxonomy taxonomy;

    public Mono<List<?>> retrieveStickerPriceData(String cik, TaxonomyReports taxonomyReports) {
        return Mono.zip(
            this.retrieveQuarterlyData(cik, taxonomyReports, QuarterlyShareholderEquity.class),
            this.retrieveQuarterlyData(cik, taxonomyReports, QuarterlyOutstandingShares.class),
            this.retrieveQuarterlyData(cik, taxonomyReports, QuarterlyFactsEPS.class),
            this.retrieveQuarterlyData(cik, taxonomyReports, QuarterlyLongTermDebt.class),
            this.retrieveQuarterlyData(cik, taxonomyReports, QuarterlyNetIncome.class))
        .flatMap(tuples -> Mono.just(
            List.of(tuples.getT1(), tuples.getT2(), tuples.getT3(),
                    tuples.getT4(), tuples.getT5())));
    }

    public <T extends AbstractQuarterlyData> Mono<List<T>> retrieveQuarterlyData(String cik, TaxonomyReports taxonomyReports,
                                                                                 Class<T> type) {
        KeysContainer keysContainer = keysMap.get(type);
        return parser.retrieveQuarterlyData(cik, taxonomyReports, taxonomy, keysContainer.keys(),
                keysContainer.deiKeys(), type);
    }
}
