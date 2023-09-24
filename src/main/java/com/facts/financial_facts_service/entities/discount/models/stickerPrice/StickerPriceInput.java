package com.facts.financial_facts_service.entities.discount.models.stickerPrice;

import com.facts.financial_facts_service.entities.discount.models.stickerPrice.types.AnnualBVPS;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.types.AnnualEPS;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.types.AnnualEquity;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.types.AnnualOperatingCashFlow;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.types.AnnualPE;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.types.AnnualROIC;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.types.AnnualRevenue;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "sticker_price_input")
public class StickerPriceInput {

    @Id
    private String cik;

    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AnnualBVPS> annualBVPS;

    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AnnualPE> annualPE;

    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AnnualEPS> annualEPS;

    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AnnualROIC> annualROIC;

    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AnnualEquity> annualEquity;

    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AnnualRevenue> annualRevenue;

    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AnnualOperatingCashFlow> annualOperatingCashFlow;

}
