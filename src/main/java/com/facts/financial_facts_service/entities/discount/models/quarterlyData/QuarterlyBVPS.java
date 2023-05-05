package com.facts.financial_facts_service.entities.discount.models.quarterlyData;

import com.facts.financial_facts_service.constants.Constants;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "quarterly_bvps", schema = Constants.FINANCIAL_FACTS)
public class QuarterlyBVPS extends AbstractQuarterlyData {
}
