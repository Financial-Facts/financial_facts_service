package com.facts.financial_facts_service.entities.discount.models.quarterlyData;

import com.facts.financial_facts_service.entities.models.QuarterlyData;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "quarterly_bvps")
public class QuarterlyBVPS extends QuarterlyData {
}
