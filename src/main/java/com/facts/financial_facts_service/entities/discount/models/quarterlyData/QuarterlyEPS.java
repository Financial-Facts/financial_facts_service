package com.facts.financial_facts_service.entities.discount.models.quarterlyData;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "quarterly_eps", schema = "financial_facts")
public class QuarterlyEPS extends AbstractQuarterlyData {
}
