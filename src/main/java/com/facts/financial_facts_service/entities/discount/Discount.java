package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.entities.discount.models.benchmarkRatioPrice.BenchmarkRatioPrice;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.StickerPrice;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

import static com.facts.financial_facts_service.constants.interfaces.Constants.CIK_REGEX;


@Entity
@Getter
@Setter
@NoArgsConstructor(force = true)
@ToString
@Table(name = "discount")
public class Discount {

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
    private StickerPrice stickerPrice;

}
