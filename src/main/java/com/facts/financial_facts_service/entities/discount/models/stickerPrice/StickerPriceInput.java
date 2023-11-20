package com.facts.financial_facts_service.entities.discount.models.stickerPrice;

import com.facts.financial_facts_service.entities.discount.interfaces.Copyable;
import com.facts.financial_facts_service.entities.discount.models.PeriodicData;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.types.AnnualBVPS;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.types.AnnualEPS;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.types.AnnualEquity;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.types.AnnualOperatingCashFlow;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.types.AnnualPE;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.types.AnnualROIC;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.types.AnnualRevenue;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "sticker_price_input")
@JsonIgnoreProperties(value = { "cik" }, allowSetters = true)
public class StickerPriceInput implements Copyable<StickerPriceInput> {

    @Id
    private String cik;

    private Double debtYears;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnnualBVPS> annualBVPS;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnnualPE> annualPE;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnnualEPS> annualEPS;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnnualROIC> annualROIC;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnnualEquity> annualEquity;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnnualRevenue> annualRevenue;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnnualOperatingCashFlow> annualOperatingCashFlow;

    @Override
    public void copy(StickerPriceInput update) {
        this.cik = update.getCik();
        this.debtYears = update.getDebtYears();
        this.replacePeriodicData(update);
    }

    private <T extends PeriodicData> void replacePeriodicData(StickerPriceInput update) {
        updatePeriodicData(this.annualBVPS, update.getAnnualBVPS());
        updatePeriodicData(this.annualPE, update.getAnnualPE());
        updatePeriodicData(this.annualEPS, update.getAnnualEPS());
        updatePeriodicData(this.annualROIC, update.getAnnualROIC());
        updatePeriodicData(this.annualEquity, update.getAnnualEquity());
        updatePeriodicData(this.annualRevenue, update.getAnnualRevenue());
        updatePeriodicData(this.annualOperatingCashFlow, update.getAnnualOperatingCashFlow());
    }

    private <T extends PeriodicData> void updatePeriodicData(List<T> current, List<T> update) {
        current.clear();
        current.addAll(update);
    }
}
