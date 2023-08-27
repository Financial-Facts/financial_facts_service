package com.facts.financial_facts_service.datafetcher.records;


import com.facts.financial_facts_service.entities.balanceSheet.BalanceSheet;
import com.facts.financial_facts_service.entities.incomeStatement.IncomeStatement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Statements {

    private List<IncomeStatement> incomeStatements;

    private List<BalanceSheet> balanceSheets;

}
