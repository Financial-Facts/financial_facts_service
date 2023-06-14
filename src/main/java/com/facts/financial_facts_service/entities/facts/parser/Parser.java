package com.facts.financial_facts_service.entities.facts.parser;

import com.facts.financial_facts_service.constants.Taxonomy;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.AbstractQuarterlyData;
import org.json.JSONObject;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public class Parser {

    private String cik;

    private JSONObject facts;

    public Mono<AbstractQuarterlyData> retrieveQuarterlyData(List<String> factsKeys, Taxonomy taxonomy,
                                                             Optional<List<String>> deiFactsKeys) {
//        const data = this.parse_facts_for_data(factsKeys, taxonomyType, deiFactsKeys);
//        const hasStartDate = this.checkHasStartDate(data);
//
//        return hasStartDate ?
//                this.populate_quarterly_data_with_start_date(this.cik, data) :
//                this.populate_quarterly_data_without_start_date(this.cik, data);
        return null;
    }

//
//        private parse_facts_for_data(
//                factsKeys: string[],
//                taxonomyType: TaxonomyType,
//                deiFactsKeys: string[]
//        ): UnitsData {
//            let data: UnitsData | null;
//            data = this.parse(factsKeys, taxonomyType);
//            if (data === null && deiFactsKeys.length !== 0) {
//                data = this.parse(deiFactsKeys);
//            }
//            if (data === null) {
//                throw new DataRetrievalException(`Keys ${factsKeys} invalid for cik ${this.cik}`);
//            }
//            return data;
//        }
//
//        private parse(
//                keys: string[],
//                taxonomyType?: TaxonomyType
//        ): UnitsData | null {
//            let result: UnitsData | null;
//            if (taxonomyType) {
//                result = this.processKeys(keys, taxonomyType);
//            } else {
//                result = this.processKeys(keys, CONSTANTS.STICKER_PRICE.DEI)
//            }
//            return result;
//        }
//
//        private processKeys(keys: string[], taxonomyType: TaxonomyType | string): UnitsData | null {
//        const lengthMap: Record<number, string> = {};
//            let max = 0;
//            keys.forEach(key => {
//            if (this.facts[taxonomyType][key]) {
//                const unitsData: UnitsData = this.facts[taxonomyType][key];
//                const unitsKey: string = Object.keys(unitsData.units)[0];
//                const unitsLength: number = unitsData.units[unitsKey].length;
//                if (unitsLength > max) {
//                    max = unitsLength;
//                }
//                lengthMap[unitsLength] = key;
//            }
//        });
//            if  (max !== 0) {
//                return this.facts[taxonomyType][lengthMap[max]];
//            }
//            return null;
//        }
//
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
