package com.facts.financial_facts_service.entities.facts.models.quarterlyData;

import com.facts.financial_facts_service.entities.models.QuarterlyData;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "quarterly_facts_eps")
public class QuarterlyFactsEPS extends QuarterlyData {
}
