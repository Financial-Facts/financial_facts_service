package com.facts.financial_facts_service.entities.discount.models.stickerPrice;

import com.facts.financial_facts_service.entities.discount.interfaces.Copyable;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.trailingPriceData.TfyPriceData;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.trailingPriceData.TtmPriceData;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.trailingPriceData.TtyPriceData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "sticker_price")
@JsonIgnoreProperties(value = { "cik" }, allowSetters = true)
public class StickerPrice implements Copyable<StickerPrice> {

    @Id
    private String cik;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cik")
    private TtmPriceData ttmPriceData;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cik")
    private TfyPriceData tfyPriceData;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cik")
    private TtyPriceData ttyPriceData;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cik")
    private StickerPriceInput input;

    @Override
    public void copy(StickerPrice update) {
        this.cik = update.getCik();
        this.ttmPriceData.copy(update.getTtmPriceData());
        this.tfyPriceData.copy(update.getTfyPriceData());
        this.ttyPriceData.copy(update.getTtyPriceData());
        this.input.copy(update.getInput());
    }
}
