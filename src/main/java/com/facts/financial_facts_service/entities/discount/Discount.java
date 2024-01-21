package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.entities.discount.interfaces.Copyable;
import com.facts.financial_facts_service.entities.discount.models.benchmarkRatioPrice.BenchmarkRatioPrice;
import com.facts.financial_facts_service.entities.discount.models.discountedCashFlow.DiscountedCashFlowPrice;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.StickerPrice;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;

import static com.facts.financial_facts_service.constants.interfaces.Constants.CIK_REGEX;


@Entity
@Getter
@Setter
@NoArgsConstructor(force = true)
@ToString
@Table(name = "discount")
public class Discount implements Copyable<Discount> {

    @Id
    @NonNull
    @Pattern(regexp = CIK_REGEX)
    private String cik;

    @NonNull
    @NotBlank
    private String symbol;

    @NonNull
    @NotBlank
    private String name;

    private LocalDate lastUpdated;

    @NotNull
    private Boolean active;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cik")
    private BenchmarkRatioPrice benchmarkRatioPrice;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cik")
    private DiscountedCashFlowPrice discountedCashFlowPrice;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cik")
    private StickerPrice stickerPrice;

    @Override
    public void copy(Discount update) {
        this.cik = update.getCik();
        this.symbol = update.getSymbol();
        this.name = update.getName();
        this.lastUpdated = LocalDate.now();
        this.active = update.getActive();
        if (Objects.nonNull(this.discountedCashFlowPrice)) {
            this.discountedCashFlowPrice.copy(update.getDiscountedCashFlowPrice());
        } else {
            this.discountedCashFlowPrice = update.getDiscountedCashFlowPrice();
        }

        if (Objects.nonNull(this.benchmarkRatioPrice)) {
            this.benchmarkRatioPrice.copy(update.getBenchmarkRatioPrice());
        } else {
            this.benchmarkRatioPrice = update.getBenchmarkRatioPrice();
        }

        if (Objects.nonNull(this.stickerPrice)) {
            this.stickerPrice.copy(update.getStickerPrice());
        } else {
            this.stickerPrice = update.getStickerPrice();
        }
    }

}
