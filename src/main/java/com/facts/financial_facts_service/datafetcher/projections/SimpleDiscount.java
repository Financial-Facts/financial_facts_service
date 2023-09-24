package com.facts.financial_facts_service.datafetcher.projections;

public interface SimpleDiscount {

    String getCik();
    String getSymbol();
    String getName();
    Boolean getActive();
    Double getRatio_Price();
    Double getTtmSalePrice();
    Double getTfySalePrice();
    Double getTtySalePrice();

}
