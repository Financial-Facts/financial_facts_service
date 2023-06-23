package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyBVPS;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyPE;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyROIC;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.TfyPriceData;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.TtmPriceData;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.TtyPriceData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

import java.util.List;

import static com.facts.financial_facts_service.constants.Constants.CIK_REGEX;


@Data
@Entity
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(schema = Constants.FINANCIAL_FACTS)
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

    private Double ratioPrice;

    private LocalDate lastUpdated;

    @OneToOne
    @PrimaryKeyJoinColumn
    private TtmPriceData ttmPriceData;

    @OneToOne
    @PrimaryKeyJoinColumn
    private TfyPriceData tfyPriceData;

    @OneToOne
    @PrimaryKeyJoinColumn
    private TtyPriceData ttyPriceData;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private List<QuarterlyBVPS> quarterlyBVPS;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private List<QuarterlyPE> quarterlyPE;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private List<QuarterlyEPS> quarterlyEPS;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private List<QuarterlyROIC> quarterlyROIC;

}
