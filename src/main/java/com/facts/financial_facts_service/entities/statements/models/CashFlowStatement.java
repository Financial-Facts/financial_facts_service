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
public class CashFlowStatement extends Statement {

    private Long netIncome;
    private Long depreciationAndAmortization;
    private Long deferredIncomeTax;
    private Long stockBasedCompensation;
    private Long changeInWorkingCapital;
    private Long accountsReceivables;
    private Long inventory;
    private Long accountsPayables;
    private Long otherWorkingCapital;
    private Long otherNonCashItems;
    private Long netCashProvidedByOperatingActivities;
    private Long investmentsInPropertyPlantAndEquipment;
    private Long acquisitionsNet;
    private Long purchasesOfInvestments;
    private Long salesMaturitiesOfInvestments;
    private Long otherInvestingActivites;
    private Long netCashUsedForInvestingActivites;
    private Long debtRepayment;
    private Long commonStockIssued;
    private Long commonStockRepurchased;
    private Long dividendsPaid;
    private Long otherFinancingActivites;
    private Long netCashUsedProvidedByFinancingActivities;
    private Long effectOfForexChangesOnCash;
    private Long netChangeInCash;
    private Long cashAtEndOfPeriod;
    private Long cashAtBeginningOfPeriod;
    private Long operatingCashFlow;
    private Long capitalExpenditure;
    private Long freeCashFlow;
    private String link;
    private String finalLink;

}
