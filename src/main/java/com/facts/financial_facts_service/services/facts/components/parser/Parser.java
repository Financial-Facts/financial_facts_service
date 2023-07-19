package com.facts.financial_facts_service.services.facts.components.parser;

import com.amazonaws.util.CollectionUtils;
import com.facts.financial_facts_service.constants.Taxonomy;
import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.facts.models.Period;
import com.facts.financial_facts_service.entities.facts.models.UnitData;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.exceptions.FeatureNotImplementedException;
import com.facts.financial_facts_service.exceptions.InsufficientKeysException;
import com.facts.financial_facts_service.exceptions.QuarterlyDataMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class Parser {

    Logger logger = LoggerFactory.getLogger(Parser.class);

    public <T extends AbstractQuarterlyData> Mono<List<T>> retrieveQuarterlyData(String cik,
                 TaxonomyReports taxonomyReports, Taxonomy taxonomy, List<String> factsKeys,
                                         List<String> deiFactsKeys, Class<T> type) {
        UnitData data = parseFactsForData(cik, taxonomyReports, taxonomy, factsKeys, deiFactsKeys);
        Map<String, List<Period>> units = data.getUnits();
        String unitKey = units.keySet().stream().toList().get(0);
        checkIsSupportedUnits(cik, unitKey);
        List<Period> periods = units.get(unitKey);
        boolean hasStartDate = checkHasStartDate(periods.get(0));
        boolean isShares = unitKey.equalsIgnoreCase("shares");
        return Mono.just(hasStartDate ?
                populateQuarterlyDataWithStartDate(cik, periods, isShares, type) :
                populateQuarterlyDataWithoutStartDate(cik, periods, type));
    }

    private UnitData parseFactsForData(String cik, TaxonomyReports taxonomyReports, Taxonomy taxonomy,
                                       List<String> factsKeys, List<String> deiFactsKeys) {
        UnitData data = this.parse(cik, taxonomyReports, taxonomy, factsKeys, false);
        if (Objects.isNull(data) && !CollectionUtils.isNullOrEmpty(deiFactsKeys)) {
            data = this.parse(cik, taxonomyReports, taxonomy, deiFactsKeys, true);
        }
        if (Objects.isNull(data)) {
            logger.error("Completed parsing {} with insufficient keys error using keys {}", cik, factsKeys);
            throw new InsufficientKeysException("Key(s) " + factsKeys + " not sufficient for " + cik);
        }
        return data;
    }

    private UnitData parse(String cik, TaxonomyReports taxonomyReports, Taxonomy taxonomy, List<String> keys, boolean checkDEI) {
        if (checkDEI) {
            return this.processKeys(cik, taxonomyReports, keys, Taxonomy.DEI);
        }
        return this.processKeys(cik, taxonomyReports, keys, taxonomy);
    }

    private UnitData processKeys(String cik, TaxonomyReports taxonomyReports, List<String> keys, Taxonomy taxonomy) {
        Map<Integer, UnitData> lengthMap = new HashMap<>();
        int max = 0;
        Map<String, UnitData> reportedValues = fetchReportedValues(cik, taxonomyReports, taxonomy);
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
                } else if (Objects.nonNull(period.getFp()) && period.getFp().equalsIgnoreCase("FY")) {
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
                    ((Objects.nonNull(period.getFp()) && period.getFp().contains("Q")) ||
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
            throw new QuarterlyDataMappingException(
                    String.format("Mapping to type %s failed for %s", type, cik));
        }
    }

    private Map<String, UnitData> fetchReportedValues(String cik, TaxonomyReports taxonomyReports, Taxonomy taxonomy) {
        Map<String, UnitData> reports = switch (taxonomy) {
            case US_GAAP -> taxonomyReports.getGaap();
            case IFRS_FULL -> taxonomyReports.getIfrs();
            case DEI -> taxonomyReports.getDei();
        };
        if (Objects.isNull(reports)) {
            logger.error("Parsing complete for {} with error: missing supported taxonomy", cik);
            throw new DataNotFoundException(taxonomy + " not found for " + cik);
        }
        return reports;
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
