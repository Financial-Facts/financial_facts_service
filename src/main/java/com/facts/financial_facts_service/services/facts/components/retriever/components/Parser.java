package com.facts.financial_facts_service.services.facts.components.retriever.components;

import com.amazonaws.util.CollectionUtils;
import com.facts.financial_facts_service.constants.enums.Taxonomy;
import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.facts.models.Period;
import com.facts.financial_facts_service.entities.facts.models.UnitData;
import com.facts.financial_facts_service.entities.models.QuarterlyData;
import com.facts.financial_facts_service.exceptions.FeatureNotImplementedException;
import com.facts.financial_facts_service.exceptions.InsufficientKeysException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class Parser {

    final Logger logger = LoggerFactory.getLogger(Parser.class);

    private final Pattern frameInstantaneousDataPattern = Pattern.compile("CY\\d\\d\\d\\dQ\\dI$");

    public Mono<List<QuarterlyData>> parseReportsForQuarterlyData(String cik, TaxonomyReports taxonomyReports,
                                                                   List<String> factsKeys, List<String> deiFactsKeys) {
        UnitData data = parseFactsForData(cik, taxonomyReports, factsKeys, deiFactsKeys);
        Map<String, List<Period>> units = data.getUnits();
        String unitKey = units.keySet().stream().toList().get(0);
        checkIsSupportedUnits(cik, unitKey);
        List<Period> periods = units.get(unitKey);
        boolean hasStartDate = Objects.nonNull(periods.get(0).getStart());
        boolean isShares = unitKey.equalsIgnoreCase("shares");
        return Mono.just(hasStartDate ?
                populateQuarterlyDataWithStartDate(cik, periods, isShares) :
                populateQuarterlyDataWithoutStartDate(cik, periods));
    }

    private UnitData parseFactsForData(String cik, TaxonomyReports taxonomyReports,
                                 List<String> factsKeys, List<String> deiFactsKeys) {
        Taxonomy primaryTaxonomy = taxonomyReports.getPrimaryTaxonomy();
        Map<String, UnitData> reportedValues = fetchReportedValues(taxonomyReports, primaryTaxonomy);
        UnitData data = this.processKeys(factsKeys, reportedValues);
        if (Objects.isNull(data) && !CollectionUtils.isNullOrEmpty(deiFactsKeys)) {
            reportedValues = fetchReportedValues(taxonomyReports, Taxonomy.DEI);
            data = this.processKeys(deiFactsKeys, reportedValues);
        }
        if (Objects.isNull(data)) {
            logger.error("Completed parsing {} with insufficient keys error using keys {}", cik, factsKeys);
            throw new InsufficientKeysException("Key(s) " + factsKeys + " not sufficient for " + cik);
        }
        return data;
    }

    private UnitData processKeys(List<String> keys, Map<String, UnitData> reportedValues) {
        Map<Integer, UnitData> lengthMap = new HashMap<>();
        int max = 0;
        for(String key: keys) {
            if (reportedValues.containsKey(key)) {
                UnitData unitData = reportedValues.get(key);
                Map<String, List<Period>> units = unitData.getUnits();
                String unitKey = units.keySet().stream().toList().get(0);
                int dataLength = units.get(unitKey).size();
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

    private List<QuarterlyData> populateQuarterlyDataWithStartDate(String cik,
                                              List<Period> periods, boolean isShares) {
        List<QuarterlyData> quarterlyData = new ArrayList<>();

        Map<Integer, Period[]> fyMap = this.buildFyMap(periods, isShares);
        fyMap.keySet().stream().sorted().forEach(key -> {
            Period[] quarters = fyMap.get(key);
            boolean hasAllQuarters = Arrays.stream(quarters).allMatch(Objects::nonNull);
            if (hasAllQuarters) {
                BigDecimal annualSum = new BigDecimal(0);
                for (Period quarter : quarters) {
                    if (Objects.nonNull(quarter.getEnd()) && Objects.nonNull(quarter.getStart())) {
                        if (ChronoUnit.DAYS.between(quarter.getStart(), quarter.getEnd()) <= 121) {
                            annualSum = annualSum.add(quarter.getVal());
                            quarterlyData.add(mapPeriodToQuarterlyData(cik, quarter));
                        } else {
                            quarter.setVal(isShares ? quarter.getVal() : quarter.getVal().subtract(annualSum));
                            quarterlyData.add(mapPeriodToQuarterlyData(cik, quarter));
                            annualSum = new BigDecimal(0);
                        }
                    }
                }
            }
        });
        return quarterlyData;
    }

    private List<QuarterlyData> populateQuarterlyDataWithoutStartDate(String cik, List<Period> periods) {
        List<QuarterlyData> quarterlyData = new ArrayList<>();
        Map<Integer, Period[]> fyMap = this.buildFyMap(periods, true);
        fyMap.keySet().stream().sorted().forEach(key -> {
            Period[] quarters = fyMap.get(key);
            Arrays.stream(quarters).forEach(quarter -> {
                if (Objects.nonNull(quarter)) {
                    quarterlyData.add(mapPeriodToQuarterlyData(cik, quarter));
                }
            });
        });
        return quarterlyData;
    }

    private Map<Integer, Period[]> buildFyMap(List<Period> periods, boolean includeInstantData) {
        Map<Integer, Period[]> fiscalYearsMap = new HashMap<>();
        this.getFilteredPeriods(periods, includeInstantData)
            .forEach(period -> {
                int fiscalYear = getFiscalYear(period);
                if (Objects.isNull(fiscalYearsMap.get(fiscalYear))) {
                    fiscalYearsMap.put(fiscalYear, new Period[4]);
                }
                int index = getFiscalYearIndex(period);
                fiscalYearsMap.get(fiscalYear)[index] = period;
            });
        return fiscalYearsMap;
    }

    private List<Period> getFilteredPeriods(List<Period> periods, boolean includeInstantData) {
        Set<LocalDate> processedDates = new HashSet<>();
        List<Period> unframedPeriods = periods.stream().filter(period ->
                Objects.isNull(period.getFrame())).toList();
        List<Period> framedPeriods = periods.stream().filter(period -> {
            if (Objects.nonNull(period.getFrame())) {
                boolean isInstantData = frameInstantaneousDataPattern.matcher(period.getFrame()).find();
                if ((includeInstantData || !isInstantData) && !processedDates.contains(period.getFiled())) {
                    processedDates.add(period.getFiled());
                    return true;
                }
            }
            return false;
        }).toList();
        return unframedPeriods.size() >= framedPeriods.size()
                ? unframedPeriods
                : framedPeriods;
    }

    private int getFiscalYear(Period period) {
        if (Objects.isNull(period.getFrame())) {
            return period.getFy();
        }
        return Integer.parseInt(period.getFrame().substring(2, 6));
    }

    private QuarterlyData mapPeriodToQuarterlyData (String cik, Period period) {
        QuarterlyData quarter = new QuarterlyData();
        quarter.setCik(cik);
        quarter.setAnnouncedDate(period.getFiled());
        quarter.setValue(period.getVal());
        return quarter;
    }

    private Map<String, UnitData> fetchReportedValues(TaxonomyReports taxonomyReports, Taxonomy taxonomy) {
        return switch (taxonomy) {
            case US_GAAP -> taxonomyReports.getGaap();
            case IFRS_FULL -> taxonomyReports.getIfrs();
            case DEI -> taxonomyReports.getDei();
        };
    }

    private void checkIsSupportedUnits(String cik, String unitKey) {
        Set<String> supportedUnits = Set.of("USD", "USD/shares", "shares");
        if (supportedUnits.contains(unitKey)) {
            return;
        }
        logger.error("Parsing complete for {} with error: currency not supported", cik);
        throw new FeatureNotImplementedException("Currency " + unitKey+ " not currently supported");
    }

    private int getFiscalYearIndex(Period period) {
        String identifier = Objects.isNull(period.getFrame())
                ? period.getFp()
                : period.getFrame();
        if (identifier.contains("Q1")) return 0;
        if (identifier.contains("Q2")) return 1;
        if (identifier.contains("Q3")) return 2;
        return 3;
    }
}
