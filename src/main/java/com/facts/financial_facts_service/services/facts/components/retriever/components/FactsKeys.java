package com.facts.financial_facts_service.services.facts.components.retriever.components;

import com.facts.financial_facts_service.services.facts.components.retriever.model.TaxonomyKeysContainer;

import java.util.Collections;
import java.util.List;

public class FactsKeys {


    public static final TaxonomyKeysContainer SHAREHOLDER_EQUITY_KEYS = new TaxonomyKeysContainer(
            List.of("StockholdersEquity",
                    "LiabilitiesAndStockholdersEquity"),
            List.of("Equity"),
            Collections.emptyList()
    );

    public static final TaxonomyKeysContainer OUTSTANDING_SHARES_KEYS = new TaxonomyKeysContainer(
            List.of("CommonStockSharesOutstanding",
                    "CommonStockSharesIssued",
                    "WeightedAverageNumberOfSharesOutstandingBasic"),
            List.of("NumberOfSharesOutstanding"),
            List.of("EntityCommonStockSharesOutstanding")
    );

    public static final TaxonomyKeysContainer EARNINGS_PER_SHARE_KEYS = new TaxonomyKeysContainer(
            List.of("EarningsPerShareBasic",
                    "NetIncomeLossPerOutstandingLimitedPartnershipUnit"),
            List.of("BasicEarningsLossPerShare",
                    "BasicAndDilutedEarningsLossPerShare"),
            Collections.emptyList()
    );

    public static final TaxonomyKeysContainer LONG_TERM_DEBT_KEYS = new TaxonomyKeysContainer(
            List.of("LongTermDebt"),
            List.of("LongTermDebt"),
            Collections.emptyList()
    );

    public static final TaxonomyKeysContainer NET_INCOME_KEYS = new TaxonomyKeysContainer(
            List.of("NetIncomeLoss"),
            List.of("NetIncomeLoss"),
            Collections.emptyList()
    );
}
