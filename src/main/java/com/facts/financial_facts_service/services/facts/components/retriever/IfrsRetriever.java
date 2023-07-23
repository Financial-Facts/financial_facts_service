package com.facts.financial_facts_service.services.facts.components.retriever;

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
public class IfrsRetriever extends AbstractRetriever implements IRetriever {

    @PostConstruct
    public void init() {
        primaryTaxonomyKeysMap = new HashMap<>();
        primaryTaxonomyKeysMap.put(QuarterlyShareholderEquity.class, SHAREHOLDER_EQUITY_KEYS.ifrsKeys());
        primaryTaxonomyKeysMap.put(QuarterlyOutstandingShares.class, OUTSTANDING_SHARES_KEYS.ifrsKeys());
        primaryTaxonomyKeysMap.put(QuarterlyFactsEPS.class, EARNINGS_PER_SHARE_KEYS.ifrsKeys());
        primaryTaxonomyKeysMap.put(QuarterlyLongTermDebt.class, LONG_TERM_DEBT_KEYS.ifrsKeys());
        primaryTaxonomyKeysMap.put(QuarterlyNetIncome.class, NET_INCOME_KEYS.ifrsKeys());
    }
}