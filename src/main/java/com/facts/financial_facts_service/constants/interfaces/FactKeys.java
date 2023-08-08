package com.facts.financial_facts_service.constants.interfaces;

import com.facts.financial_facts_service.services.facts.components.retriever.models.KeysContainer;

import java.util.Collections;
import java.util.List;

public interface FactKeys {

    KeysContainer SHAREHOLDER_EQUITY_KEYS = new KeysContainer(
            List.of("StockholdersEquity",
                    "LiabilitiesAndStockholdersEquity"),
            List.of("Equity"),
            Collections.emptyList()
    );

    KeysContainer OUTSTANDING_SHARES_KEYS = new KeysContainer(
            List.of("CommonStockSharesOutstanding",
                    "CommonStockSharesIssued",
                    "WeightedAverageNumberOfSharesOutstandingBasic"),
            List.of("NumberOfSharesOutstanding"),
            List.of("EntityCommonStockSharesOutstanding")
    );

    KeysContainer EARNINGS_PER_SHARE_KEYS = new KeysContainer(
            List.of("EarningsPerShareBasic",
                    "NetIncomeLossPerOutstandingLimitedPartnershipUnit"),
            List.of("BasicEarningsLossPerShare",
                    "BasicAndDilutedEarningsLossPerShare"),
            Collections.emptyList()
    );

    KeysContainer LONG_TERM_DEBT_KEYS = new KeysContainer(
            List.of("LongTermDebt"),
            List.of("LongTermDebt"),
            Collections.emptyList()
    );

    KeysContainer NET_INCOME_KEYS = new KeysContainer(
            List.of("NetIncomeLoss"),
            List.of("NetIncomeLoss"),
            Collections.emptyList()
    );
}
