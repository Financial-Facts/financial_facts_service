package com.facts.financial_facts_service.services.facts.components.retriever;

import com.facts.financial_facts_service.constants.interfaces.Constants;
import com.facts.financial_facts_service.constants.interfaces.FactKeys;
import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyFactsEPS;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyNetIncome;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyShareholderEquity;
import com.facts.financial_facts_service.entities.models.QuarterlyData;
import com.facts.financial_facts_service.services.facts.components.retriever.components.Parser;
import com.facts.financial_facts_service.services.facts.components.retriever.models.StickerPriceQuarterlyData;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class AbstractRetriever implements IRetriever, Constants, FactKeys {

    private final Parser parser = new Parser();

    protected Map<Class<? extends QuarterlyData>, List<String>> primaryTaxonomyKeysMap;

    protected final Map<Class<? extends QuarterlyData>, List<String>> deiKeysMap = buildDeiKeysMap();

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
        return parser.parseReportsForQuarterlyData(cik, taxonomyReports,
                        primaryTaxonomyKeysMap.get(type), deiKeysMap.get(type))
            .map(quarterlyData -> {
                // It is safe to suppress the warning because we are down casting from Quarterly Data Object to child
                @SuppressWarnings("unchecked")
                List<T> castQuarterlyData = (List<T>) quarterlyData;
                return castQuarterlyData;
            });
    }

    private Map<Class<? extends QuarterlyData>, List<String>> buildDeiKeysMap() {
        Map<Class<? extends QuarterlyData>, List<String>> deiKeysMap = new HashMap<>();
        deiKeysMap.put(QuarterlyShareholderEquity.class, SHAREHOLDER_EQUITY_KEYS.deiKeys());
        deiKeysMap.put(QuarterlyOutstandingShares.class, OUTSTANDING_SHARES_KEYS.deiKeys());
        deiKeysMap.put(QuarterlyFactsEPS.class, EARNINGS_PER_SHARE_KEYS.deiKeys());
        deiKeysMap.put(QuarterlyLongTermDebt.class, LONG_TERM_DEBT_KEYS.deiKeys());
        deiKeysMap.put(QuarterlyNetIncome.class, NET_INCOME_KEYS.deiKeys());
        return deiKeysMap;
    }
}
