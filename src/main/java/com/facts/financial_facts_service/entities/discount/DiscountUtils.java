package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.entities.discount.models.quarterlyData.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.AbstractTrailingPriceData;

import java.util.List;
import java.util.Objects;

public class DiscountUtils {

    protected static void assignPeriodDataCik(Discount discount, String cik) {
        setIfNonNull(discount.getTtmPriceData(), cik, false);
        setIfNonNull(discount.getTfyPriceData(), cik, false);
        setIfNonNull(discount.getTtyPriceData(), cik, false);
        setIfNonNull(discount.getQuarterlyBVPS(), cik, true);
        setIfNonNull(discount.getQuarterlyPE(), cik, true);
        setIfNonNull(discount.getQuarterlyEPS(), cik, true);
        setIfNonNull(discount.getQuarterlyROIC(), cik, true);
    }

    private static <T> void setIfNonNull(List<T> periodData, String cik, boolean isQuarterly) {
        if (!isQuarterly && Objects.nonNull(periodData)) {
            setTrailingDataCik(periodData, cik);
        }
        if (isQuarterly && Objects.nonNull(periodData)) {
            setQuarterlyDataCik(periodData, cik);
        }
    }

    private static <T> void setTrailingDataCik(List<T> priceData, String cik) {
        priceData.forEach(period -> ((AbstractTrailingPriceData) period).setCik(cik));
    }

    private static <T> void setQuarterlyDataCik(List<T> quarterlyData, String cik) {
        quarterlyData.forEach(period -> ((AbstractQuarterlyData) period).setCik(cik));
    }

}
