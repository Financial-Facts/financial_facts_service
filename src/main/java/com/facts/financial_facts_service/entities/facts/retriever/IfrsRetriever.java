package com.facts.financial_facts_service.entities.facts.retriever;

import com.facts.financial_facts_service.entities.discount.models.quarterlyData.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import com.facts.financial_facts_service.entities.facts.retriever.models.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.retriever.models.QuarterlyShareholderEquity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class IfrsRetriever implements IRetriever {

    private String cik;

    private JSONObject facts;

    @Override
    public List<QuarterlyShareholderEquity> retrieve_quarterly_shareholder_equity() {
        return null;
    }

    @Override
    public List<QuarterlyOutstandingShares> retrieve_quarterly_outstanding_shares() {
        return null;
    }

    @Override
    public List<QuarterlyEPS> retrieve_quarterly_EPS() {
        return null;
    }

    @Override
    public List<AbstractQuarterlyData> retrieve_quarterly_long_term_debt() {
        return null;
    }

    @Override
    public List<List<AbstractQuarterlyData>> retrieve_quarterly_long_term_debt_parts() {
        return null;
    }

    @Override
    public Double retrieve_benchmark_ratio_price(Double benchmark) {
        return null;
    }

    @Override
    public List<AbstractQuarterlyData> retrieve_quarterly_net_income() {
        return null;
    }

    @Override
    public List<AbstractQuarterlyData> retrieve_quarterly_total_debt() {
        return null;
    }

    @Override
    public List<AbstractQuarterlyData> retrieve_quarterly_total_assets() {
        return null;
    }

    @Override
    public List<AbstractQuarterlyData> retrieve_quarterly_total_cash() {
        return null;
    }
}
