package com.facts.financial_facts_service.entities.discount.models.trailingPriceData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class AbstractTrailingPriceData {

    @Id
    @JsonIgnore
    private String cik;

    private Double stickerPrice;

    private Double salePrice;

}
