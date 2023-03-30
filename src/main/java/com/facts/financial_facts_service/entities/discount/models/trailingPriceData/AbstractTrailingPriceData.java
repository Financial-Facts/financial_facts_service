package com.facts.financial_facts_service.entities.discount.models.trailingPriceData;

import jakarta.persistence.*;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class AbstractTrailingPriceData {

    @Id
    private String cik;

    private Double stickerPrice;

    private Double salePrice;

}
