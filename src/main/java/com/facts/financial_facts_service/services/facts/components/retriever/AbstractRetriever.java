package com.facts.financial_facts_service.services.facts.components.retriever;

import com.facts.financial_facts_service.configurations.TaxonomyKeyMap.models.TaxonomyKeyMap;
import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyFactsEPS;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyNetIncome;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyShareholderEquity;
import com.facts.financial_facts_service.entities.models.QuarterlyData;
import com.facts.financial_facts_service.services.facts.components.parser.Parser;
import com.facts.financial_facts_service.services.facts.components.retriever.models.KeysContainer;
import com.facts.financial_facts_service.services.facts.components.retriever.models.StickerPriceQuarterlyData;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


public abstract class AbstractRetriever implements IRetriever, Constants {

    @Autowired
    private Parser parser;

    @Autowired
    protected TaxonomyKeyMap taxonomyKeyMap;

    protected Map<Class<? extends QuarterlyData>, KeysContainer> keyMap;

    public Mono<StickerPriceQuarterlyData> retrieveStickerPriceData(String cik, TaxonomyReports taxonomyReports) {
        return Mono.zip(
            this.retrieveQuarterlyData(cik, taxonomyReports, QuarterlyShareholderEquity.class),
            this.retrieveQuarterlyData(cik, taxonomyReports, QuarterlyOutstandingShares.class),
            this.retrieveQuarterlyData(cik, taxonomyReports, QuarterlyFactsEPS.class),
            this.retrieveQuarterlyData(cik, taxonomyReports, QuarterlyLongTermDebt.class),
            this.retrieveQuarterlyData(cik, taxonomyReports, QuarterlyNetIncome.class))
        .flatMap(tuples -> Mono.just(StickerPriceQuarterlyData.builder()
            .quarterlyShareholderEquity(tuples.getT1())
            .quarterlyOutstandingShares(tuples.getT2())
            .quarterlyFactsEPS(tuples.getT3())
            .quarterlyLongTermDebt(tuples.getT4())
            .quarterlyNetIncome(tuples.getT5()).build()));
    }

    private <T extends QuarterlyData> Mono<List<T>> retrieveQuarterlyData(String cik,
                                      TaxonomyReports taxonomyReports, Class<T> type) {
        KeysContainer keysContainer = keyMap.get(type);
        return parser.retrieveQuarterlyData(cik, taxonomyReports, keysContainer.keys(), keysContainer.deiKeys())
            .flatMap(quarterlyData -> {
                // It is safe to suppress the warning because we are down casting from Quarterly Data Object to child
                @SuppressWarnings("unchecked")
                List<T> castQuarterlyData = (List<T>) quarterlyData;
                return Mono.just(castQuarterlyData);
            });
    }
}
