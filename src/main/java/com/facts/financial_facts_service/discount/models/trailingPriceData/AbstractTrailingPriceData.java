package com.facts.financial_facts_service.discount.models.trailingPriceData;

import com.facts.financial_facts_service.discount.Discount;
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
