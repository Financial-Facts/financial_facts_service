package com.facts.financial_facts_service.services.facts.components.retriever;

import com.facts.financial_facts_service.constants.interfaces.Constants;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyFactsEPS;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyNetIncome;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyShareholderEquity;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;


@Getter
@Setter
@Component
public class GaapRetriever extends AbstractRetriever implements IRetriever, Constants {

    @PostConstruct
    public void init() {
        primaryTaxonomyKeysMap = new HashMap<>();
        primaryTaxonomyKeysMap.put(QuarterlyShareholderEquity.class, SHAREHOLDER_EQUITY_KEYS.gaapKeys());
        primaryTaxonomyKeysMap.put(QuarterlyOutstandingShares.class, OUTSTANDING_SHARES_KEYS.gaapKeys());
        primaryTaxonomyKeysMap.put(QuarterlyFactsEPS.class, EARNINGS_PER_SHARE_KEYS.gaapKeys());
        primaryTaxonomyKeysMap.put(QuarterlyLongTermDebt.class, LONG_TERM_DEBT_KEYS.gaapKeys());
        primaryTaxonomyKeysMap.put(QuarterlyNetIncome.class, NET_INCOME_KEYS.gaapKeys());
    }
}
