package com.facts.financial_facts_service.constants.interfaces;

public interface Queries {

    String getAllSimpleDiscounts =
            "select a.cik, " +
            "a.symbol, " +
            "a.name, " +
            "a.active, " +
            "b.ratio_price, " +
            "c.sale_price AS \"ttmSalePrice\", " +
            "d.sale_price AS \"tfySalePrice\", " +
            "e.sale_price AS \"ttySalePrice\" " +
            "FROM discount a,  " +
            "benchmark_ratio_price b, " +
            "ttm_price_data c, " +
            "tfy_price_data d, " +
            "tty_price_data e " +
            "WHERE a.cik=b.cik AND a.cik=c.cik AND a.cik=d.cik AND a.cik=e.cik;";

}
