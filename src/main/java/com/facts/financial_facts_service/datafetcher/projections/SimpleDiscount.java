package com.facts.financial_facts_service.datafetcher.projections;

public interface SimpleDiscount {

    String getCik();
    String getSymbol();
    String getName();
    Double getRatioPrice();
    Double getTtmSalePrice();
    Double getTfySalePrice();
    Double getTtySalePrice();

}
