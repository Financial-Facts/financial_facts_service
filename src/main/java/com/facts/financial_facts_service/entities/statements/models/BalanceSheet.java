package com.facts.financial_facts_service.entities.statements.models;

import com.facts.financial_facts_service.entities.statements.Statement;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BalanceSheet extends Statement {

    private Long cashAndCashEquivalents;
    private Long shortTermInvestments;
    private Long cashAndShortTermInvestments;
    private Long netReceivables;
    private Long inventory;
    private Long otherCurrentAssets;
    private Long totalCurrentAssets;
    private Long propertyPlantEquipmentNet;
    private Long goodwill;
    private Long intangibleAssets;
    private Long goodwillAndIntangibleAssets;
    private Long longTermInvestments;
    private Long taxAssets;
    private Long otherNonCurrentAssets;
    private Long totalNonCurrentAssets;
    private Long otherAssets;
    private Long totalAssets;
    private Long accountPayables;
    private Long shortTermDebt;
    private Long taxPayables;
    private Long deferredRevenue;
    private Long otherCurrentLiabilities;
    private Long totalCurrentLiabilities;
    private Long longTermDebt;
    private Long deferredRevenueNonCurrent;
    private Long deferredTaxLiabilitiesNonCurrent;
    private Long otherNonCurrentLiabilities;
    private Long totalNonCurrentLiabilities;
    private Long otherLiabilities;
    private Long capitalLeaseObligations;
    private Long totalLiabilities;
    private Long preferredStock;
    private Long commonStock;
    private Long retainedEarnings;
    private Long accumulatedOtherComprehensiveIncomeLoss;
    private Long othertotalStockholdersEquity;
    private Long totalStockholdersEquity;
    private Long totalEquity;
    private Long totalLiabilitiesAndStockholdersEquity;
    private Long minorityInterest;
    private Long totalLiabilitiesAndTotalEquity;
    private Long totalInvestments;
    private Long totalDebt;
    private Long netDebt;
    private String link;
    private String finalLink;

}
