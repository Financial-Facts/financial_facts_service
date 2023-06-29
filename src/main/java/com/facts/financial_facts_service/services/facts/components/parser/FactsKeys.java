package com.facts.financial_facts_service.services.facts.components.parser;

import java.util.List;

public class FactsKeys {

    public static List<String> shareholderEquity = List.of(
        "StockholdersEquity",
        "LiabilitiesAndStockholdersEquity"
    );

    public static List<String> outstandingShares = List.of(
        "CommonStockSharesOutstanding",
        "CommonStockSharesIssued",
        "WeightedAverageNumberOfSharesOutstandingBasic"
    );

    public static List<String> outstandingSharesDEI = List.of(
        "EntityCommonStockSharesOutstanding"
    );

    public static List<String> earningsPerShare = List.of(
        "EarningsPerShareBasic",
        "NetIncomeLossPerOutstandingLimitedPartnershipUnit"
    );

    public static List<String> longTermDebt = List.of(
        "LongTermDebt"
    );

    public static List<String> netIncome = List.of(
        "NetIncomeLoss"
    );
}
