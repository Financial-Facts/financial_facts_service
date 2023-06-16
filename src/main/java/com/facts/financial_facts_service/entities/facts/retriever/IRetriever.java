package com.facts.financial_facts_service.entities.facts.retriever;

import com.facts.financial_facts_service.entities.discount.models.quarterlyData.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import com.facts.financial_facts_service.entities.facts.retriever.models.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.entities.facts.retriever.models.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.retriever.models.QuarterlyShareholderEquity;
import reactor.core.publisher.Mono;

import java.util.List;


public interface IRetriever {

    Mono<List<QuarterlyShareholderEquity>> retrieve_quarterly_shareholder_equity();
    Mono<List<QuarterlyOutstandingShares>> retrieve_quarterly_outstanding_shares();
    Mono<List<QuarterlyEPS>> retrieve_quarterly_EPS();
    Mono<List<QuarterlyLongTermDebt>> retrieve_quarterly_long_term_debt();
    Mono<List<List<AbstractQuarterlyData>>> retrieve_quarterly_long_term_debt_parts();
    Mono<Double> retrieve_benchmark_ratio_price(Double benchmark);
    Mono<List<AbstractQuarterlyData>> retrieve_quarterly_net_income();
    Mono<List<AbstractQuarterlyData>> retrieve_quarterly_total_debt();
    Mono<List<AbstractQuarterlyData>> retrieve_quarterly_total_assets();
    Mono<List<AbstractQuarterlyData>> retrieve_quarterly_total_cash();

}
