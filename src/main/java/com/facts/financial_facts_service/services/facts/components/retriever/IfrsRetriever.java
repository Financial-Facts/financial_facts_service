package com.facts.financial_facts_service.services.facts.components.retriever;

import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.*;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Getter
@Setter
@Component
@AllArgsConstructor
public class IfrsRetriever extends AbstractRetriever implements IRetriever {

    @Override
    public Mono<List<QuarterlyShareholderEquity>> retrieve_quarterly_shareholder_equity(String cik, TaxonomyReports taxonomyLayer) {
        return null;
    }

    @Override
    public Mono<List<QuarterlyOutstandingShares>> retrieve_quarterly_outstanding_shares(String cik, TaxonomyReports taxonomyLayer) {
        return null;
    }

    @Override
    public Mono<List<QuarterlyFactsEPS>> retrieve_quarterly_EPS(String cik, TaxonomyReports taxonomyLayer) {
        return null;
    }

    @Override
    public Mono<List<QuarterlyLongTermDebt>> retrieve_quarterly_long_term_debt(String cik, TaxonomyReports taxonomyLayer) {
        return null;
    }

    public Mono<List<List<AbstractQuarterlyData>>> retrieve_quarterly_long_term_debt_parts() {
        return null;
    }

    @Override
    public Mono<Double> retrieve_benchmark_ratio_price(Double benchmark) {
        return null;
    }

    @Override
    public Mono<List<QuarterlyNetIncome>> retrieve_quarterly_net_income(String cik, TaxonomyReports taxonomyReports) {
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