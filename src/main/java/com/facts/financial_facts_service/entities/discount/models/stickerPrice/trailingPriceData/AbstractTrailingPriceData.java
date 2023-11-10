package com.facts.financial_facts_service.entities.discount.models.stickerPrice.trailingPriceData;


import com.facts.financial_facts_service.entities.discount.interfaces.Copyable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@JsonIgnoreProperties(value = { "cik" }, allowSetters = true)
public abstract class AbstractTrailingPriceData implements Copyable<AbstractTrailingPriceData> {

    @Id
    private String cik;

    private Double stickerPrice;

    private Double salePrice;

    @Override
    public void copy(AbstractTrailingPriceData update) {
        this.cik = update.getCik();
        this.stickerPrice = update.getStickerPrice();
        this.salePrice = update.getSalePrice();
    }

}
