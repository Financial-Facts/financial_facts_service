package com.facts.financial_facts_service.services.facts.components.retriever;

import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.*;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface IRetriever {

    Mono<List<?>> fetchQuarterlyData(Set<String> params, String cik, TaxonomyReports taxonomyReports);
    Mono<List<QuarterlyShareholderEquity>> retrieve_quarterly_shareholder_equity(String cik, TaxonomyReports taxonomyReports);
    Mono<List<QuarterlyOutstandingShares>> retrieve_quarterly_outstanding_shares(String cik, TaxonomyReports taxonomyReports);
    Mono<List<QuarterlyFactsEPS>> retrieve_quarterly_EPS(String cik, TaxonomyReports taxonomyLayer);
    Mono<List<QuarterlyLongTermDebt>> retrieve_quarterly_long_term_debt(String cik, TaxonomyReports taxonomyReports);
    Mono<Double> retrieve_benchmark_ratio_price(Double benchmark);
    Mono<List<QuarterlyNetIncome>> retrieve_quarterly_net_income(String cik, TaxonomyReports taxonomyReports);
    Mono<List<AbstractQuarterlyData>> retrieve_quarterly_total_debt();
    Mono<List<AbstractQuarterlyData>> retrieve_quarterly_total_assets();
    Mono<List<AbstractQuarterlyData>> retrieve_quarterly_total_cash();

}
