package com.facts.financial_facts_service.datafetcher.records;

import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.*;
import com.facts.financial_facts_service.entities.identity.Identity;

import java.util.List;

public record StickerPriceData(String cik,
                               String symbol,
                               String name,
                               List<QuarterlyShareholderEquity> quarterlyShareholderEquity,
                               List<QuarterlyOutstandingShares> quarterlyOutstandingShares,
                               List<QuarterlyFactsEPS> quarterlyEPS,
                               List<QuarterlyLongTermDebt> quarterlyLongTermDebt,
                               List<QuarterlyNetIncome> quarterlyNetIncome) {

    public StickerPriceData(Identity identity, Facts facts) {
        this(identity.getCik(),
            identity.getSymbol(),
            identity.getName(),
            facts.getQuarterlyShareholderEquity(),
            facts.getQuarterlyOutstandingShares(),
            facts.getQuarterlyEPS(),
            facts.getQuarterlyLongTermDebt(),
            facts.getQuarterlyNetIncome());
    }

}
