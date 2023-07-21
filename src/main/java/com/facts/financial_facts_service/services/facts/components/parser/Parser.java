package com.facts.financial_facts_service.services.facts.components.parser;

import com.amazonaws.util.CollectionUtils;
import com.facts.financial_facts_service.constants.Taxonomy;
import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.facts.models.Period;
import com.facts.financial_facts_service.entities.facts.models.UnitData;
import com.facts.financial_facts_service.entities.models.QuarterlyData;
import com.facts.financial_facts_service.exceptions.FeatureNotImplementedException;
import com.facts.financial_facts_service.exceptions.InsufficientKeysException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
public class Parser {

    Logger logger = LoggerFactory.getLogger(Parser.class);

    public Mono<List<QuarterlyData>> retrieveQuarterlyData(String cik, TaxonomyReports taxonomyReports,
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
        Set<LocalDate> processedEndDates = new HashSet<>();
        BigDecimal annualSum = new BigDecimal(0);
        for (Period period: periods) {
            if (Objects.nonNull(period.getEnd()) && Objects.nonNull(period.getStart()) &&
                    !processedEndDates.contains(period.getEnd())) {
                if (ChronoUnit.DAYS.between(period.getStart(), period.getEnd()) < 105) {
                    annualSum = annualSum.add(period.getVal());
                    quarterlyData.add(mapPeriodToQuarterlyData(cik, period));
                    processedEndDates.add(period.getEnd());
                } else if (Objects.nonNull(period.getFp()) && period.getFp().equalsIgnoreCase("FY")) {
                    period.setVal(isShares ? period.getVal() : period.getVal().subtract(annualSum));
                    quarterlyData.add(mapPeriodToQuarterlyData(cik, period));
                    processedEndDates.add(period.getEnd());
                    annualSum = new BigDecimal(0);
                }
            }
        }
        return quarterlyData;
    }

    private List<QuarterlyData> populateQuarterlyDataWithoutStartDate(String cik, List<Period> periods) {
        List<QuarterlyData> quarterlyData = new ArrayList<>();
        Set<LocalDate> processedEndDates = new HashSet<>();
        for (Period period: periods) {
            if (!processedEndDates.contains(period.getEnd()) &&
                    ((Objects.nonNull(period.getFp()) && period.getFp().contains("Q")) ||
                    (Objects.nonNull(period.getFrame()) && period.getFrame().contains("Q")))) {
                quarterlyData.add(mapPeriodToQuarterlyData(cik, period));
                processedEndDates.add(period.getEnd());
            }
        }
        return quarterlyData;
    }

    private QuarterlyData mapPeriodToQuarterlyData (String cik, Period period) {
        QuarterlyData quarter = new QuarterlyData();
        quarter.setCik(cik);
        quarter.setAnnouncedDate(period.getEnd());
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
}
