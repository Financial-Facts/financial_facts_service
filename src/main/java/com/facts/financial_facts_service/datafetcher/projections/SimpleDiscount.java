package com.facts.financial_facts_service.datafetcher.projections;

import java.time.LocalDate;

public interface SimpleDiscount {

    String getCik();
    String getSymbol();
    String getName();
    Boolean getActive();
    LocalDate getLastUpdated();
    Double getBenchmarkRatioPrice();
    Double getDiscountedCashFlowPrice();
    Double getStickerPrice();

}
