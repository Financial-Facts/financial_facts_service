package com.facts.financial_facts_service.services.facts.components.parser;

import com.facts.financial_facts_service.constants.Taxonomy;
import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.facts.models.Period;
import com.facts.financial_facts_service.entities.facts.models.UnitData;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.exceptions.FeatureNotImplementedException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@AllArgsConstructor
public class Parser {

    public <T extends AbstractQuarterlyData> Mono<List<T>> retrieveQuarterlyData(String cik,
                 TaxonomyReports taxonomyReports, Taxonomy taxonomy, List<String> factsKeys,
                                         Optional<List<String>> deiFactsKeys, Class<T> type) {
        UnitData data = parseFactsForData(cik, taxonomyReports, taxonomy, factsKeys, deiFactsKeys);
        String unitKey = data.getUnits().keySet().stream().toList().get(0);
        checkIsSupportedUnits(unitKey);
        List<Period> periods = data.getUnits().get(unitKey);
        boolean hasStartDate = checkHasStartDate(periods.get(0));
        boolean isShares = unitKey.equalsIgnoreCase("shares");
        return Mono.just(hasStartDate ?
                populateQuarterlyDataWithStartDate(cik, periods, isShares, type) :
                populateQuarterlyDataWithoutStartDate(cik, periods, type));
    }

    private UnitData parseFactsForData(String cik, TaxonomyReports taxonomyReports, Taxonomy taxonomy,
                                       List<String> factsKeys, Optional<List<String>> deiFactsKeys) {
        UnitData data = this.parse(taxonomyReports, taxonomy, factsKeys, false);
        if (Objects.isNull(data) && deiFactsKeys.isPresent()) {
            data = this.parse(taxonomyReports, taxonomy, deiFactsKeys.get(), true);
        }
        if (Objects.isNull(data)) {
            throw new DataNotFoundException("Keys " + factsKeys + " not sufficient for " + cik);
        }
        return data;
    }

    private UnitData parse(TaxonomyReports taxonomyReports, Taxonomy taxonomy, List<String> keys, boolean checkDEI) {
        if (checkDEI) {
            return this.processKeys(taxonomyReports, keys, Taxonomy.DEI);
        }
        return this.processKeys(taxonomyReports, keys, taxonomy);
    }

    private UnitData processKeys(TaxonomyReports taxonomyReports, List<String> keys, Taxonomy taxonomy) {
        Map<Integer, UnitData> lengthMap = new HashMap<>();
        int max = 0;
        Map<String, UnitData> reportedValues = fetchReportedValues(taxonomyReports, taxonomy);
        for(String key: keys) {
            if (reportedValues.containsKey(key)) {
                UnitData unitData = reportedValues.get(key);
                String unitKey = unitData.getUnits().keySet().stream().toList().get(0);
                int dataLength = unitData.getUnits().get(unitKey).size();
                if (dataLength > max) {
                    max = dataLength;
                }
                lengthMap.put(dataLength, unitData);
            }
        }
        if (max != 0) {
            return lengthMap.get(max);
        }
        return null;
    }

    private <T extends AbstractQuarterlyData> List<T> populateQuarterlyDataWithStartDate(String cik,
                                             List<Period> periods, boolean isShares, Class<T> type) {
        List<T> quarterlyData = new ArrayList<>();
        Set<LocalDate> processedEndDates = new HashSet<>();
        BigDecimal annualSum = new BigDecimal(0);
        for (Period period: periods) {
            if (Objects.nonNull(period.getEnd()) && Objects.nonNull(period.getStart()) &&
                    !processedEndDates.contains(period.getEnd())) {
                if (ChronoUnit.DAYS.between(period.getStart(), period.getEnd()) < 105) {
                    annualSum = annualSum.add(period.getVal());
                    quarterlyData.add(mapPeriodToQuarterlyData(cik, period, type));
                    processedEndDates.add(period.getEnd());
                } else if (period.getFp().equalsIgnoreCase("FY")) {
                    period.setVal(isShares ? period.getVal() : period.getVal().subtract(annualSum));
                    quarterlyData.add(mapPeriodToQuarterlyData(cik, period, type));
                    processedEndDates.add(period.getEnd());
                    annualSum = new BigDecimal(0);
                }
            }
        }
        return quarterlyData;
    }

    private <T extends AbstractQuarterlyData> List<T> populateQuarterlyDataWithoutStartDate(String cik,
                                                                                            List<Period> periods,
                                                                                            Class<T> type) {
        List<T> quarterlyData = new ArrayList<>();
        Set<LocalDate> processedEndDates = new HashSet<>();
        for (Period period: periods) {
            if (!processedEndDates.contains(period.getEnd()) &&
                    (period.getFp().contains("Q") ||
                    (Objects.nonNull(period.getFrame()) && period.getFrame().contains("Q")))) {
                quarterlyData.add(mapPeriodToQuarterlyData(cik, period, type));
                processedEndDates.add(period.getEnd());
            }
        }
        return quarterlyData;
    }

    private boolean checkHasStartDate(Period period) {
        return Objects.nonNull(period.getStart());
    }

    private <T extends AbstractQuarterlyData> T mapPeriodToQuarterlyData (String cik,
                                                        Period period, Class<T> type) {
        T quarter;
        try {
            quarter = type.getDeclaredConstructor().newInstance();
            quarter.setCik(cik);
            quarter.setAnnouncedDate(period.getEnd());
            quarter.setValue(period.getVal());
            return type.cast(quarter);
        } catch (InvocationTargetException | NoSuchMethodException |
                    InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, UnitData> fetchReportedValues(TaxonomyReports taxonomyReports, Taxonomy taxonomy) {
        return switch (taxonomy) {
            case US_GAAP -> taxonomyReports.getGaap();
            case IFRS_FULL -> taxonomyReports.getIfrs();
            case DEI -> taxonomyReports.getDei();
        };
    }

    private void checkIsSupportedUnits(String unitKey) {
        if (unitKey.equalsIgnoreCase("USD") ||
            unitKey.equalsIgnoreCase("USD/shares") ||
            unitKey.equalsIgnoreCase("shares")) {
            return;
        }
        throw new FeatureNotImplementedException("Currency " + unitKey+ " not currently supported");
    }
}
