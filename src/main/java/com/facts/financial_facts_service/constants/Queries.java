package com.facts.financial_facts_service.constants;

public interface Queries {

    String getAllActiveSimpleDiscounts =
            "SELECT n.cik, " +
            "n.symbol, " +
            "n.name, " +
            "n.ratio_price, " +
            "ttm.sale_price AS \"ttmSalePrice\", " +
            "tfy.sale_price AS \"tfySalePrice\", " +
            "tty.sale_price AS \"ttySalePrice\" " +
            "FROM (select cik, symbol, name, ratio_price, active FROM discount d WHERE d.active = true) as n " +
            "INNER JOIN (select cik, sale_price from ttm_price_data) AS ttm ON n.cik=ttm.cik " +
            "INNER JOIN (select cik, sale_price from tfy_price_data) AS tfy ON n.cik=tfy.cik " +
            "INNER JOIN (select cik, sale_price from tty_price_data) AS tty ON n.cik=tty.cik;";

    String getAllCikForActiveDiscounts =
            "SELECT cik FROM discount d WHERE d.active = true";
}
