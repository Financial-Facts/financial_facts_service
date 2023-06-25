package com.facts.financial_facts_service.entities.facts.models.records;

import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyShareholderEquity;

import java.util.List;

public record StickerPriceData(String cik,
                               List<QuarterlyShareholderEquity> quarterlyShareholderEquity,
                               List<QuarterlyOutstandingShares> quarterlyOutstandingShares,
                               List<QuarterlyEPS> quarterlyEPS,
                               List<QuarterlyLongTermDebt> quarterlyLongTermDebt) {

    public StickerPriceData(Facts facts) {
        this(facts.getCik(),
            facts.getQuarterlyShareholderEquity(),
            facts.getQuarterlyOutstandingShares(),
            facts.getQuarterlyEPS(),
            facts.getQuarterlyLongTermDebt());
    }

}
