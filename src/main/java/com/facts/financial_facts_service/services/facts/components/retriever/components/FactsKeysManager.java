package com.facts.financial_facts_service.services.facts.components.retriever.components;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.Taxonomy;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyFactsEPS;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyNetIncome;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyShareholderEquity;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import com.facts.financial_facts_service.services.facts.components.retriever.model.KeysContainer;
import com.facts.financial_facts_service.services.facts.components.retriever.model.TaxonomyKeysContainer;
import jakarta.annotation.PostConstruct;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Order(-1)
@Component
public class FactsKeysManager extends FactsKeys implements Constants {

    private Map<Taxonomy, Map<Class<? extends AbstractQuarterlyData>, KeysContainer>> taxonomyToKeysMap;

    private Map<Class<? extends AbstractQuarterlyData>, TaxonomyKeysContainer> typeToKeysContainerMap;

    public Map<Class<? extends AbstractQuarterlyData>, KeysContainer> getKeysMapForTaxonomy(Taxonomy taxonomy) {
        return taxonomyToKeysMap.get(taxonomy);
    }

    @PostConstruct
    private void init() {
        typeToKeysContainerMap = buildTypeToKeysContainerMap();
        taxonomyToKeysMap = buildTaxonomyToKeysMap();
    }

    private Map<Taxonomy, Map<Class<? extends AbstractQuarterlyData>, KeysContainer>> buildTaxonomyToKeysMap() {
        Map<Taxonomy, Map<Class<? extends AbstractQuarterlyData>, KeysContainer>> taxonomyMapping = new HashMap<>();
        taxonomyMapping.put(Taxonomy.US_GAAP, buildClassToKeysContainerMapping(Taxonomy.US_GAAP));
        taxonomyMapping.put(Taxonomy.IFRS_FULL, buildClassToKeysContainerMapping(Taxonomy.IFRS_FULL));
        return taxonomyMapping;
    }

    private Map<Class<? extends AbstractQuarterlyData>, KeysContainer> buildClassToKeysContainerMapping(Taxonomy taxonomy) {
        Map<Class<? extends AbstractQuarterlyData>, KeysContainer> dataClassMapping = new HashMap<>();
        addAllKeysToClassToKeysContainerMapping(dataClassMapping, taxonomy);
        return dataClassMapping;

    }

    private void addAllKeysToClassToKeysContainerMapping(Map<Class<? extends AbstractQuarterlyData>,
                                                     KeysContainer> dataClassMapping, Taxonomy taxonomy) {
        addTypeKeysToClassMapping(dataClassMapping, taxonomy, QuarterlyShareholderEquity.class);
        addTypeKeysToClassMapping(dataClassMapping, taxonomy, QuarterlyOutstandingShares.class);
        addTypeKeysToClassMapping(dataClassMapping, taxonomy, QuarterlyFactsEPS.class);
        addTypeKeysToClassMapping(dataClassMapping, taxonomy, QuarterlyLongTermDebt.class);
        addTypeKeysToClassMapping(dataClassMapping, taxonomy, QuarterlyNetIncome.class);
    }

    private void addTypeKeysToClassMapping(Map<Class<? extends AbstractQuarterlyData>, KeysContainer> dataClassMapping,
                                                  Taxonomy taxonomy, Class<? extends AbstractQuarterlyData> type) {
        if (taxonomy.equals(Taxonomy.US_GAAP)) {
            TaxonomyKeysContainer taxonomyKeysContainer = typeToKeysContainerMap.get(type);
            dataClassMapping.put(type, new KeysContainer(
                taxonomyKeysContainer.getGaapKeys(),
                taxonomyKeysContainer.getDeiKeys()));
        } else if (taxonomy.equals(Taxonomy.IFRS_FULL)) {
            TaxonomyKeysContainer taxonomyKeysContainer = typeToKeysContainerMap.get(type);
            dataClassMapping.put(type, new KeysContainer(
                    taxonomyKeysContainer.getIfrsKeys(),
                    taxonomyKeysContainer.getDeiKeys()));
        }
    }

    private Map<Class<? extends AbstractQuarterlyData>, TaxonomyKeysContainer> buildTypeToKeysContainerMap() {
        Map<Class<? extends AbstractQuarterlyData>, TaxonomyKeysContainer> map = new HashMap<>();
        map.put(QuarterlyShareholderEquity.class, SHAREHOLDER_EQUITY_KEYS);
        map.put(QuarterlyOutstandingShares.class, OUTSTANDING_SHARES_KEYS);
        map.put(QuarterlyFactsEPS.class, EARNINGS_PER_SHARE_KEYS);
        map.put(QuarterlyLongTermDebt.class, LONG_TERM_DEBT_KEYS);
        map.put(QuarterlyNetIncome.class, NET_INCOME_KEYS);
        return map;
    }
}
