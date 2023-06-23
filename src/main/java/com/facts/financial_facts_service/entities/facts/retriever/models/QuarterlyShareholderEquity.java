package com.facts.financial_facts_service.entities.facts.retriever.models;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "quarterly_shareholder_equity", schema = Constants.FINANCIAL_FACTS)
public class QuarterlyShareholderEquity extends AbstractQuarterlyData {
}
