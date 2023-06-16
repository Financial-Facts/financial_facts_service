package com.facts.financial_facts_service.entities.facts.parser;

import com.facts.financial_facts_service.constants.Taxonomy;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.facts.parser.models.UnitData;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import reactor.core.publisher.Mono;

import java.util.*;

@AllArgsConstructor
public class Parser {

    private String cik;

    private JSONObject facts;

    private Taxonomy taxonomy;

    public <T> Mono<List<T>> retrieveQuarterlyData(List<String> factsKeys,
                                                             Optional<List<String>> deiFactsKeys) {
        UnitData data = parseFactsForData(factsKeys, deiFactsKeys);
//        const data = this.parse_facts_for_data(factsKeys, taxonomyType, deiFactsKeys);
//        const hasStartDate = this.checkHasStartDate(data);
//
//        return hasStartDate ?
//                this.populate_quarterly_data_with_start_date(this.cik, data) :
//                this.populate_quarterly_data_without_start_date(this.cik, data);
        return null;
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
        Map<Integer, String> lengthMap = new HashMap<>();
        int max = 0;
        JSONObject financialFacts = this.facts.getJSONObject(taxonomyType);
        for(String key: keys) {
            if (financialFacts.has(key)) {
                UnitData unitData = (UnitData) financialFacts.get(key);
                String unitKey = unitData.getUnits().keys().next();
                int dataLength = unitData.getUnits().getJSONArray(unitKey).length();
                if (dataLength > max) {
                    max = dataLength;
                }
                lengthMap.put(dataLength, key);
            }
        }
        if (max != 0) {
            return (UnitData) financialFacts.get(lengthMap.get(max));
        }
        return null;
    }

//        private checkHasStartDate(data: any): boolean {
//        const units = data[CONSTANTS.STICKER_PRICE.UNITS];
//        const quarter = units[Object.keys(units)[0]][0];
//            return quarter.start !== undefined && quarter.start !== null;
//        }
//
//        private populate_quarterly_data_with_start_date(cik: string, data: UnitsData): QuarterlyData[] {
//        const quarterly_data: QuarterlyData[] = [];
//        const processed_end_dates: string[] = []
//        const key: string = Object.keys(data.units)[0];
//        const isShares = key === 'shares';
//            let annualSum: number = 0;
//            data.units[key]
//                    .forEach((period: Period) => {
//                if (period.end &&
//                        period.start &&
//                        !processed_end_dates.includes(period.end)) {
//                    if (days_between(new Date(period.start), new Date(period.end)) < 105) {
//                        // ToDo: Convert other currencies to USD
//                        annualSum += period.val;
//                        quarterly_data.push({
//                                cik: cik,
//                                announcedDate: new Date(period.end),
//                                value: period.val
//                            });
//                        processed_end_dates.push(period.end);
//                    } else if (period.fp === 'FY') {
//                        quarterly_data.push({
//                                cik: cik,
//                                announcedDate: new Date(period.end),
//                                value: isShares ? period.val : period.val - annualSum
//                            });
//                        processed_end_dates.push(period.end);
//                        annualSum = 0;
//                    }
//                }
//            });
//            return quarterly_data;
//        }
//
//        private populate_quarterly_data_without_start_date(cik: string, data: any): QuarterlyData[] {
//        const quarterly_data: QuarterlyData[] = [];
//        const key = Object.keys(data.units)[0];
//        const processed_end_dates: string[] = []
//            data.units[key]
//                    .forEach((period: Period) => {
//                if (!processed_end_dates.includes(period.end) &&
//                        (period.fp.includes('Q') || period.frame && period.frame.includes('Q'))) {
//                    const val: QuarterlyData = {
//                            cik: cik,
//                            announcedDate: new Date(period.end),
//                            value: period.val
//                    }
//                    quarterly_data.push(val);
//                    processed_end_dates.push(period.end);
//                }
//            });
//            return quarterly_data;
//        }
//    }
//
//    export default Parser;
}
