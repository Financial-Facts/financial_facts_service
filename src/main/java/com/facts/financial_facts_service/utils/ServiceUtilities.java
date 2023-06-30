package com.facts.financial_facts_service.utils;

import com.amazonaws.util.StringUtils;
import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyNetIncome;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyShareholderEquity;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.AbstractTrailingPriceData;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple7;

import java.util.List;
import java.util.Objects;

public class ServiceUtilities implements Constants {

    public static void mapRetrievedQuarterlyData(Facts facts, List<?> retrievedQuarterlyData) {
        retrievedQuarterlyData.stream().forEach(dataSet -> {
            if (dataSet instanceof List<?> && !((List) dataSet).isEmpty()) {
                if (((List) dataSet).get(0) instanceof QuarterlyOutstandingShares) {
                    facts.setQuarterlyOutstandingShares((List<QuarterlyOutstandingShares>) dataSet);
                }
                if (((List) dataSet).get(0) instanceof QuarterlyShareholderEquity) {
                    facts.setQuarterlyShareholderEquity((List<QuarterlyShareholderEquity>) dataSet);
                }
                if (((List) dataSet).get(0) instanceof QuarterlyEPS) {
                    facts.setQuarterlyEPS((List<QuarterlyEPS>) dataSet);
                }
                if (((List) dataSet).get(0) instanceof QuarterlyLongTermDebt) {
                    facts.setQuarterlyLongTermDebt((List<QuarterlyLongTermDebt>) dataSet);
                }
                if (((List) dataSet).get(0) instanceof QuarterlyNetIncome) {
                    facts.setQuarterlyNetIncome((List<QuarterlyNetIncome>) dataSet);
                }
            }
        });
    }

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

    public static Mono<Tuple7<Void, Void, Void, Void, Void, Void, Void>> assignPeriodDataCik(Discount discount, String cik) {
        return Mono.zip(setTrailingIfNonNull(discount.getTtmPriceData(), cik),
            setTrailingIfNonNull(discount.getTfyPriceData(), cik),
            setTrailingIfNonNull(discount.getTtyPriceData(), cik),
            setQuarterlyIfNonNull(discount.getQuarterlyBVPS(), cik),
            setQuarterlyIfNonNull(discount.getQuarterlyPE(), cik),
            setQuarterlyIfNonNull(discount.getQuarterlyEPS(), cik),
            setQuarterlyIfNonNull(discount.getQuarterlyROIC(), cik));
    }

    private static <T> Mono<Void> setTrailingIfNonNull(T periodData, String cik) {
        if (Objects.nonNull(periodData)) {
            AbstractTrailingPriceData trailingPriceData = (AbstractTrailingPriceData) periodData;
            if (StringUtils.isNullOrEmpty(trailingPriceData.getCik())) {
                trailingPriceData.setCik(cik);
            }
        }
        return Mono.empty();
    }

    private static <T> Mono<Void> setQuarterlyIfNonNull(List<T> periodData, String cik) {
        if (Objects.nonNull(periodData)) {
            periodData.forEach(period -> {
                AbstractQuarterlyData quarterlyData = (AbstractQuarterlyData) period;
                if (StringUtils.isNullOrEmpty(quarterlyData.getCik())) {
                    quarterlyData.setCik(cik);
                }
            });
        }
        return Mono.empty();
    }
}
