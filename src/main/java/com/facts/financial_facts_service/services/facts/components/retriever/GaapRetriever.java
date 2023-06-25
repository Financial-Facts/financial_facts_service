package com.facts.financial_facts_service.services.retriever;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.Taxonomy;
import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import com.facts.financial_facts_service.services.retriever.components.parser.FactsKeys;
import com.facts.financial_facts_service.services.retriever.components.parser.Parser;
import com.facts.financial_facts_service.services.retriever.models.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.services.retriever.models.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.services.retriever.models.QuarterlyShareholderEquity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Getter
@Setter
@Component
@AllArgsConstructor
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
    public Mono<List<QuarterlyEPS>> retrieve_quarterly_EPS(String cik, TaxonomyReports taxonomyReports) {
        return parser.retrieveQuarterlyData(cik, taxonomyReports, Taxonomy.US_GAAP, FactsKeys.earningsPerShare,
                Optional.empty(), QuarterlyEPS.class);
    }

    @Override
    public Mono<List<QuarterlyLongTermDebt>> retrieve_quarterly_long_term_debt(String cik,
                                                          TaxonomyReports taxonomyReports) {
        return parser.retrieveQuarterlyData(cik, taxonomyReports, Taxonomy.US_GAAP, FactsKeys.longTermDebt,
                Optional.empty(), QuarterlyLongTermDebt.class);
    }

    @Override
    public Mono<List<List<AbstractQuarterlyData>>> retrieve_quarterly_long_term_debt_parts() {
        return null;
    }

    @Override
    public Mono<Double> retrieve_benchmark_ratio_price(Double benchmark) {
        return null;
    }

    @Override
    public Mono<List<AbstractQuarterlyData>> retrieve_quarterly_net_income() {
        return null;
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
