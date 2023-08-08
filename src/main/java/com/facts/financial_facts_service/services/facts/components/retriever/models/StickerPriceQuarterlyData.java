package com.facts.financial_facts_service.services.facts.components.retriever.models;

import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyFactsEPS;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyNetIncome;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyShareholderEquity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class StickerPriceQuarterlyData {

    private List<QuarterlyShareholderEquity> quarterlyShareholderEquity;
    private List<QuarterlyOutstandingShares> quarterlyOutstandingShares;
    private List<QuarterlyFactsEPS> quarterlyFactsEPS;
    private List<QuarterlyLongTermDebt> quarterlyLongTermDebt;
    private List<QuarterlyNetIncome> quarterlyNetIncome;

}
