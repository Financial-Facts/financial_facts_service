package com.facts.financial_facts_service.entities.discount.models.discountedCashFlow.types;

import com.facts.financial_facts_service.entities.discount.models.PeriodicData;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "historical_capital_expenditure")
public class HistoricalCapitalExpenditure extends PeriodicData {
}
