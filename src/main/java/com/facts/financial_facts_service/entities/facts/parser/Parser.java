package com.facts.financial_facts_service.entities.facts.parser;

import com.facts.financial_facts_service.constants.Taxonomy;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.facts.parser.models.Period;
import com.facts.financial_facts_service.entities.facts.parser.models.UnitData;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Parser {

    private String cik;

    private JSONObject facts;

    private Taxonomy taxonomy;

    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule()).build();

    public <T extends AbstractQuarterlyData> Mono<List<T>> retrieveQuarterlyData(List<String> factsKeys,
                                                                                 Optional<List<String>> deiFactsKeys,
                                                                                 Class<T> type) {
        UnitData data = parseFactsForData(factsKeys, deiFactsKeys);
        String unitKey = data.getUnits().keySet().stream().collect(Collectors.toList()).get(0);
        List<Period> periods = data.getUnits().get(unitKey);
        boolean hasStartDate = checkHasStartDate(periods.get(0));
        boolean isShares = unitKey.equalsIgnoreCase("shares");
        return Mono.just(hasStartDate ?
                populateQuarterlyDataWithStartDate(periods, isShares, type) :
                populateQuarterlyDataWithoutStartDate(periods, type));
    }

    private UnitData parseFactsForData(List<String> factsKeys, Optional<List<String>> deiFactsKeys) {
        UnitData data = this.parse(factsKeys, false);
        if (Objects.isNull(data) && deiFactsKeys.isPresent()) {
            data = this.parse(deiFactsKeys.get(), true);
        }
        if (Objects.isNull(data)) {
            throw new DataNotFoundException("Keys " + factsKeys + " not sufficient for " + cik);
        }
        return data;
    }

    private UnitData parse(List<String> keys, boolean checkDEI) {
        if (checkDEI) {
            return this.processKeys(keys, Taxonomy.DEI.toString());
        }
        return this.processKeys(keys, taxonomy.toString());
    }

    private UnitData processKeys(List<String> keys, String taxonomyType) {
        Map<Integer, UnitData> lengthMap = new HashMap<>();
        int max = 0;
        JSONObject financialFacts = this.facts.getJSONObject(taxonomyType);
        for(String key: keys) {
            if (financialFacts.has(key)) {
                try {
                    UnitData unitData = this.mapJsonToUnitData(financialFacts.getJSONObject(key));
                    String unitKey = unitData.getUnits().keySet().stream().collect(Collectors.toList()).get(0);
                    int dataLength = unitData.getUnits().get(unitKey).size();
                    if (dataLength > max) {
                        max = dataLength;
                    }
                    lengthMap.put(dataLength, unitData);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (max != 0) {
            return lengthMap.get(max);
        }
        return null;
    }

    private <T extends AbstractQuarterlyData> List<T> populateQuarterlyDataWithStartDate(List<Period> periods,
                                                                                         boolean isShares,
                                                                                         Class<T> type) {
        List<T> quarterlyData = new ArrayList<>();
        Set<LocalDate> processedEndDates = new HashSet<>();
        BigDecimal annualSum = new BigDecimal(0);
        for (Period period: periods) {
            if (Objects.nonNull(period.getEnd()) && Objects.nonNull(period.getStart()) &&
                    !processedEndDates.contains(period.getEnd())) {
                if (ChronoUnit.DAYS.between(period.getStart(), period.getEnd()) < 105) {
                    annualSum.add(period.getVal());
                    quarterlyData.add(mapPeriodToQuarterlyData(period, type));
                    processedEndDates.add(period.getEnd());
                } else if (period.getFp().equalsIgnoreCase("FY")) {
                    period.setVal(isShares ? period.getVal() : period.getVal().subtract(annualSum));
                    quarterlyData.add(mapPeriodToQuarterlyData(period, type));
                    processedEndDates.add(period.getEnd());
                    annualSum = new BigDecimal(0);
                }
            }
        }
        return quarterlyData;
    }

    private <T extends AbstractQuarterlyData> List<T> populateQuarterlyDataWithoutStartDate(List<Period> periods,
                                                                                            Class<T> type) {
        List<T> quarterlyData = new ArrayList<>();
        Set<LocalDate> processedEndDates = new HashSet<>();
        for (Period period: periods) {
            if (!processedEndDates.contains(period.getEnd()) &&
                    (period.getFp().contains("Q") ||
                    (Objects.nonNull(period.getFrame()) && period.getFrame().contains("Q")))) {
                quarterlyData.add(mapPeriodToQuarterlyData(period, type));
                processedEndDates.add(period.getEnd());
            }
        }
        return quarterlyData;
    }

    private boolean checkHasStartDate(Period period) {
        return Objects.nonNull(period.getStart());
    }

    private UnitData mapJsonToUnitData (JSONObject json) throws JsonProcessingException {
        return mapper.readValue(json.toString(), UnitData.class);
    }

    private <T extends AbstractQuarterlyData> T mapPeriodToQuarterlyData (Period period,
                                                                          Class<T> type) {
        T quarter = null;
        try {
            quarter = type.newInstance();
            quarter.setCik(cik);
            quarter.setAnnouncedDate(period.getEnd());
            quarter.setValue(period.getVal());
            return type.cast(quarter);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
