package com.facts.financial_facts_service.discount.models.quarterlyData;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "quarterly_bvps", schema = "financial_facts")
public class QuarterlyBVPS extends AbstractQuarterlyData {
}
