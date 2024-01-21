package com.facts.financial_facts_service.entities.discount.models.stickerPrice;

import com.facts.financial_facts_service.entities.discount.interfaces.Valuation;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name = "sticker_price")
public class StickerPrice extends Valuation<StickerPrice, StickerPriceInput> {
}
