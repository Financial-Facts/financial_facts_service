package com.facts.financial_facts_service.configurations.TaxonomyKeyMap;

import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyFactsEPS;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyNetIncome;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyShareholderEquity;
import com.facts.financial_facts_service.entities.models.QuarterlyData;
import com.facts.financial_facts_service.services.facts.components.retriever.models.AllTaxonomyKeysContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactKeys {

    protected static Map<Class<? extends QuarterlyData>, AllTaxonomyKeysContainer> buildTypeToKeysContainerMap() {
        Map<Class<? extends QuarterlyData>, AllTaxonomyKeysContainer> map = new HashMap<>();
        map.put(QuarterlyShareholderEquity.class, SHAREHOLDER_EQUITY_KEYS);
        map.put(QuarterlyOutstandingShares.class, OUTSTANDING_SHARES_KEYS);
        map.put(QuarterlyFactsEPS.class, EARNINGS_PER_SHARE_KEYS);
        map.put(QuarterlyLongTermDebt.class, LONG_TERM_DEBT_KEYS);
        map.put(QuarterlyNetIncome.class, NET_INCOME_KEYS);
        return map;
    }


    private static final AllTaxonomyKeysContainer SHAREHOLDER_EQUITY_KEYS = new AllTaxonomyKeysContainer(
            List.of("StockholdersEquity",
                    "LiabilitiesAndStockholdersEquity"),
            List.of("Equity"),
            Collections.emptyList()
    );

    private static final AllTaxonomyKeysContainer OUTSTANDING_SHARES_KEYS = new AllTaxonomyKeysContainer(
            List.of("CommonStockSharesOutstanding",
                    "CommonStockSharesIssued",
                    "WeightedAverageNumberOfSharesOutstandingBasic"),
            List.of("NumberOfSharesOutstanding"),
            List.of("EntityCommonStockSharesOutstanding")
    );

    private static final AllTaxonomyKeysContainer EARNINGS_PER_SHARE_KEYS = new AllTaxonomyKeysContainer(
            List.of("EarningsPerShareBasic",
                    "NetIncomeLossPerOutstandingLimitedPartnershipUnit"),
            List.of("BasicEarningsLossPerShare",
                    "BasicAndDilutedEarningsLossPerShare"),
            Collections.emptyList()
    );

    private static final AllTaxonomyKeysContainer LONG_TERM_DEBT_KEYS = new AllTaxonomyKeysContainer(
            List.of("LongTermDebt"),
            List.of("LongTermDebt"),
            Collections.emptyList()
    );

    private static final AllTaxonomyKeysContainer NET_INCOME_KEYS = new AllTaxonomyKeysContainer(
            List.of("NetIncomeLoss"),
            List.of("NetIncomeLoss"),
            Collections.emptyList()
    );
}
