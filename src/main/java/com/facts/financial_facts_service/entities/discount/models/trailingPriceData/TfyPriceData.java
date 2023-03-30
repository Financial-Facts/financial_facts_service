package com.facts.financial_facts_service.entities.discount.models.trailingPriceData;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tfy_price_data", schema = "financial_facts")
public class TfyPriceData extends AbstractTrailingPriceData {
}
