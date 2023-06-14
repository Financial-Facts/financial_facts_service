package com.facts.financial_facts_service.entities.facts.retriever.models;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.AbstractQuarterlyData;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "quarterly_outstanding_shares", schema = Constants.FINANCIAL_FACTS)
public class QuarterlyOutstandingShares extends AbstractQuarterlyData {
}
