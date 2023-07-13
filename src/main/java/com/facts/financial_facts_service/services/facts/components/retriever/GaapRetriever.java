package com.facts.financial_facts_service.services.facts.components.retriever;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.Taxonomy;
import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.*;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import com.facts.financial_facts_service.services.facts.components.parser.FactsKeys;
import com.facts.financial_facts_service.services.facts.components.parser.Parser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;

@Getter
@Setter
@Component
public class GaapRetriever extends AbstractRetriever implements IRetriever, Constants {

    @Autowired
    private Parser parser;

    @Override
    public Mono<List<QuarterlyShareholderEquity>> retrieve_quarterly_shareholder_equity(String cik,
                                                                   TaxonomyReports taxonomyReports) {
        return parser.retrieveQuarterlyData(cik, taxonomyReports, Taxonomy.US_GAAP, FactsKeys.shareholderEquity,
                Optional.empty(), QuarterlyShareholderEquity.class);
    }

    @Override
    public Mono<List<QuarterlyOutstandingShares>> retrieve_quarterly_outstanding_shares(String cik,
                                                                   TaxonomyReports taxonomyReports) {
        return parser.retrieveQuarterlyData(cik, taxonomyReports, Taxonomy.US_GAAP, FactsKeys.outstandingShares,
                Optional.of(FactsKeys.outstandingSharesDEI), QuarterlyOutstandingShares.class);
    }

    @Override
    public Mono<List<QuarterlyFactsEPS>> retrieve_quarterly_EPS(String cik, TaxonomyReports taxonomyReports) {
        return parser.retrieveQuarterlyData(cik, taxonomyReports, Taxonomy.US_GAAP, FactsKeys.earningsPerShare,
                Optional.empty(), QuarterlyFactsEPS.class);
    }

    @Override
    public Mono<List<QuarterlyLongTermDebt>> retrieve_quarterly_long_term_debt(String cik,
                                                          TaxonomyReports taxonomyReports) {
        return parser.retrieveQuarterlyData(cik, taxonomyReports, Taxonomy.US_GAAP,
                        FactsKeys.longTermDebt,Optional.empty(), QuarterlyLongTermDebt.class);
    }

    @Override
    public Mono<Double> retrieve_benchmark_ratio_price(Double benchmark) {
        return null;
    }

    @Override
    public Mono<List<QuarterlyNetIncome>> retrieve_quarterly_net_income(String cik,
                                                   TaxonomyReports taxonomyReports) {
        return parser.retrieveQuarterlyData(cik, taxonomyReports, Taxonomy.US_GAAP, FactsKeys.netIncome,
                Optional.empty(), QuarterlyNetIncome.class);
    }

    @Override
    public Mono<List<AbstractQuarterlyData>> retrieve_quarterly_total_debt() {
        return null;
    }

    @Override
    public Mono<List<AbstractQuarterlyData>> retrieve_quarterly_total_assets() {
        return null;
    }

    @Override
    public Mono<List<AbstractQuarterlyData>> retrieve_quarterly_total_cash() {
        return null;
    }
}
