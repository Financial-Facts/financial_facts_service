package com.facts.financial_facts_service.entities.discount.models.stickerPrice;

import com.facts.financial_facts_service.entities.discount.models.stickerPrice.trailingPriceData.TfyPriceData;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.trailingPriceData.TtmPriceData;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.trailingPriceData.TtyPriceData;
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
public class StickerPrice {

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

}
