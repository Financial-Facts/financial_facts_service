package com.facts.financial_facts_service.entities.discount.models.stickerPrice.trailingPriceData;


import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractTrailingPriceData {

    @Id
    private String cik;

    private Double stickerPrice;

    private Double salePrice;

}
