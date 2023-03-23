package com.facts.financial_facts_service.discount.models.trailingPriceData;

import com.facts.financial_facts_service.discount.Discount;
import jakarta.persistence.*;

@MappedSuperclass
public abstract class AbstractTrailingPriceData {

    @Id
    private String cik;

    @ManyToOne()
    @MapsId
    @JoinColumn(name = "cik")
    private Discount discount;

    private Double stickerPrice;

    private Double salePrice;

}
