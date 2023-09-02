package com.facts.financial_facts_service.entities.statements.models;

import com.facts.financial_facts_service.entities.statements.Statement;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomeStatement extends Statement {

    private Long revenue;
    private Long costOfRevenue;
    private Long grossProfit;
    private BigDecimal grossProfitRatio;
    private Long researchAndDevelopmentExpenses;
    private Long generalAndAdministrativeExpenses;
    private Long sellingAndMarketingExpenses;
    private Long sellingGeneralAndAdministrativeExpenses;
    private Long otherExpenses;
    private Long operatingExpenses;
    private Long costAndExpenses;
    private Long interestIncome;
    private Long interestExpense;
    private Long depreciationAndAmortization;
    private Long ebitda;
    private BigDecimal ebitdaratio;
    private Long operatingIncome;
    private BigDecimal operatingIncomeRatio;
    private Long totalOtherIncomeExpensesNet;
    private Long incomeBeforeTax;
    private BigDecimal incomeBeforeTaxRatio;
    private Long incomeTaxExpense;
    private Long netIncome;
    private BigDecimal netIncomeRatio;
    private BigDecimal eps;
    private BigDecimal epsdiluted;
    private Long weightedAverageShsOut;
    private Long weightedAverageShsOutDil;
    private String link;
    private String finalLink;

}
