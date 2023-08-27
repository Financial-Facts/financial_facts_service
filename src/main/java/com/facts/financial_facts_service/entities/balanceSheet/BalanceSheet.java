package com.facts.financial_facts_service.entities.balanceSheet;

import com.facts.financial_facts_service.entities.balanceSheet.models.BalanceSheetKey;
import com.facts.financial_facts_service.entities.incomeStatement.models.IncomeStatementKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import static com.facts.financial_facts_service.constants.interfaces.Constants.CIK_REGEX;

@Entity
@Getter
@Setter
@AllArgsConstructor
@IdClass(BalanceSheetKey.class)
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BalanceSheet {

    @Id
    @Pattern(regexp = CIK_REGEX)
    private String cik;

    @Id
    private LocalDate date;

    private String symbol;
    private String reportedCurrency;
    private LocalDate fillingDate;
    private LocalDate acceptedDate;
    private String calendarYear;
    private String period;
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
