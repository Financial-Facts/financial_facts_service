package com.facts.financial_facts_service.entities.facts.retriever;

import com.facts.financial_facts_service.entities.discount.models.quarterlyData.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import com.facts.financial_facts_service.entities.facts.retriever.models.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.retriever.models.QuarterlyShareholderEquity;

import java.util.List;

public interface IRetriever {

    List<QuarterlyShareholderEquity> retrieve_quarterly_shareholder_equity();
    List<QuarterlyOutstandingShares> retrieve_quarterly_outstanding_shares();
    List<QuarterlyEPS> retrieve_quarterly_EPS();
    List<AbstractQuarterlyData> retrieve_quarterly_long_term_debt();
    List<List<AbstractQuarterlyData>> retrieve_quarterly_long_term_debt_parts();
    Double retrieve_benchmark_ratio_price(Double benchmark);
    List<AbstractQuarterlyData> retrieve_quarterly_net_income();
    List<AbstractQuarterlyData> retrieve_quarterly_total_debt();
    List<AbstractQuarterlyData> retrieve_quarterly_total_assets();
    List<AbstractQuarterlyData> retrieve_quarterly_total_cash();

}
