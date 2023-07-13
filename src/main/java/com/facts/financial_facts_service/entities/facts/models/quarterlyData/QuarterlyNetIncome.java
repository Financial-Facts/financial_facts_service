package com.facts.financial_facts_service.entities.facts.models.quarterlyData;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "quarterly_net_income")
public class QuarterlyNetIncome extends AbstractQuarterlyData {
}