package com.facts.financial_facts_service.constants.interfaces;

public interface Queries {

    String getAllSimpleDiscounts =
            "select a.cik, " +
            "a.symbol, " +
            "a.name, " +
            "a.active, " +
            "a.last_updated AS \"LastUpdated\", " +
            "d.price AS \"discountedCashFlowPrice\", " +
            "b.price AS \"benchmarkRatioPrice\", " +
            "c.price AS \"stickerPrice\" " +
            "FROM discount a, " +
            "discounted_cash_flow_price d, " +
            "benchmark_ratio_price b, " +
            "sticker_price c " +
            "WHERE a.cik=b.cik AND a.cik=c.cik AND a.cik=d.cik;";

}
