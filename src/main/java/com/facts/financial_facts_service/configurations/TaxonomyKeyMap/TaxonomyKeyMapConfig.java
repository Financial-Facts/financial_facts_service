package com.facts.financial_facts_service.configurations.TaxonomyKeyMap;

import com.facts.financial_facts_service.configurations.TaxonomyKeyMap.models.TaxonomyKeyMap;
import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.Taxonomy;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyFactsEPS;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyNetIncome;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyShareholderEquity;
import com.facts.financial_facts_service.entities.models.QuarterlyData;
import com.facts.financial_facts_service.services.facts.components.retriever.models.KeysContainer;
import com.facts.financial_facts_service.services.facts.components.retriever.models.AllTaxonomyKeysContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class TaxonomyKeyMapConfig extends FactKeys implements Constants {

    private Map<Class<? extends QuarterlyData>, AllTaxonomyKeysContainer> allKeysMap;

    @Bean
    @Order(-1)
    public TaxonomyKeyMap taxonomyKeyMap() {
        allKeysMap = buildTypeToKeysContainerMap();
        TaxonomyKeyMap keys = new TaxonomyKeyMap();
        keys.setGaap(buildClassToKeysContainerMapping(Taxonomy.US_GAAP));
        keys.setIfrs(buildClassToKeysContainerMapping(Taxonomy.IFRS_FULL));
        return keys;
    }

    private Map<Class<? extends QuarterlyData>, KeysContainer> buildClassToKeysContainerMapping(Taxonomy taxonomy) {
        Map<Class<? extends QuarterlyData>, KeysContainer> dataClassMapping = new HashMap<>();
        addTypeKeysToClassMapping(dataClassMapping, taxonomy, QuarterlyShareholderEquity.class);
        addTypeKeysToClassMapping(dataClassMapping, taxonomy, QuarterlyOutstandingShares.class);
        addTypeKeysToClassMapping(dataClassMapping, taxonomy, QuarterlyFactsEPS.class);
        addTypeKeysToClassMapping(dataClassMapping, taxonomy, QuarterlyLongTermDebt.class);
        addTypeKeysToClassMapping(dataClassMapping, taxonomy, QuarterlyNetIncome.class);
        return dataClassMapping;

    }

    private void addTypeKeysToClassMapping(Map<Class<? extends QuarterlyData>, KeysContainer> dataClassMapping,
                                                        Taxonomy taxonomy, Class<? extends QuarterlyData> type) {
        AllTaxonomyKeysContainer taxonomyKeysContainer = allKeysMap.get(type);
        List<String> keys = taxonomy.equals(Taxonomy.US_GAAP)
                ? taxonomyKeysContainer.getGaapKeys()
                : taxonomyKeysContainer.getIfrsKeys();
        dataClassMapping.put(type, new KeysContainer(keys, taxonomyKeysContainer.getDeiKeys()));
    }
}
