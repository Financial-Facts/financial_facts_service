package com.facts.financial_facts_service.entities.discount.models.stickerPrice.trailingPriceData;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tfy_price_data")
@AllArgsConstructor
public class TfyPriceData extends AbstractTrailingPriceData {
}
