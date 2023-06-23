package com.facts.financial_facts_service.utils;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.AbstractTrailingPriceData;

import java.util.List;
import java.util.Objects;

public class ServiceUtilities implements Constants {

    public static String padSimpleCik(String simpleCik) {
        StringBuilder result = new StringBuilder();
        result.append(CIK).append(simpleCik);
        if (result.length() > 13) {
            result.replace(13, result.length(), EMPTY);
        }
        while (result.length() < 13) {
            result.replace(3, 3, ZERO);
        }
        return result.toString();
    }

    public static void assignPeriodDataCik(Discount discount, String cik) {
        setTrailingIfNonNull(discount.getTtmPriceData(), cik);
        setTrailingIfNonNull(discount.getTfyPriceData(), cik);
        setTrailingIfNonNull(discount.getTtyPriceData(), cik);
        setQuarterlyIfNonNull(discount.getQuarterlyBVPS(), cik);
        setQuarterlyIfNonNull(discount.getQuarterlyPE(), cik);
        setQuarterlyIfNonNull(discount.getQuarterlyEPS(), cik);
        setQuarterlyIfNonNull(discount.getQuarterlyROIC(), cik);
    }

    private static <T> void setTrailingIfNonNull(T periodData, String cik) {
        if (Objects.nonNull(periodData)) {
            ((AbstractTrailingPriceData) periodData).setCik(cik);
        }
    }

    private static <T> void setQuarterlyIfNonNull(List<T> periodData, String cik) {
        if (Objects.nonNull(periodData)) {
            periodData.forEach(period -> ((AbstractQuarterlyData) period).setCik(cik));
        }
    }
}
