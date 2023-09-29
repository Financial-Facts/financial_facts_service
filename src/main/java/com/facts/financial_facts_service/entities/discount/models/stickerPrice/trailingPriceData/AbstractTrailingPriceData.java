package com.facts.financial_facts_service.entities.discount.models.stickerPrice.trailingPriceData;


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
public abstract class AbstractTrailingPriceData {

    @Id
    private String cik;

    private Double stickerPrice;

    private Double salePrice;

}
