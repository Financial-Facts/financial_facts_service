package com.facts.financial_facts_service.entities.facts.models.quarterlyData;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "quarterly_long_term_debt", schema = Constants.FINANCIAL_FACTS)
public class QuarterlyLongTermDebt extends AbstractQuarterlyData {
}
