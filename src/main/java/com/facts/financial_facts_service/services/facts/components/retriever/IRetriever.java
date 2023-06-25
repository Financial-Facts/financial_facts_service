package com.facts.financial_facts_service.services.retriever;

import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import com.facts.financial_facts_service.services.retriever.models.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.services.retriever.models.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.services.retriever.models.QuarterlyShareholderEquity;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface IRetriever {

    Mono<List<?>> fetchQuarterlyData(Set<String> params, String cik, TaxonomyReports taxonomyLayer);
    Mono<List<QuarterlyShareholderEquity>> retrieve_quarterly_shareholder_equity(String cik, TaxonomyReports taxonomyLayer);
    Mono<List<QuarterlyOutstandingShares>> retrieve_quarterly_outstanding_shares(String cik, TaxonomyReports taxonomyLayer);
    Mono<List<QuarterlyEPS>> retrieve_quarterly_EPS(String cik, TaxonomyReports taxonomyLayer);
    Mono<List<QuarterlyLongTermDebt>> retrieve_quarterly_long_term_debt(String cik, TaxonomyReports taxonomyLayer);
    Mono<List<List<AbstractQuarterlyData>>> retrieve_quarterly_long_term_debt_parts();
    Mono<Double> retrieve_benchmark_ratio_price(Double benchmark);
    Mono<List<AbstractQuarterlyData>> retrieve_quarterly_net_income();
    Mono<List<AbstractQuarterlyData>> retrieve_quarterly_total_debt();
    Mono<List<AbstractQuarterlyData>> retrieve_quarterly_total_assets();
    Mono<List<AbstractQuarterlyData>> retrieve_quarterly_total_cash();

}
