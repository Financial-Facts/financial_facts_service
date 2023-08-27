package com.facts.financial_facts_service.entities.incomeStatement;

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

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.facts.financial_facts_service.constants.interfaces.Constants.CIK_REGEX;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@IdClass(IncomeStatementKey.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomeStatement {

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
