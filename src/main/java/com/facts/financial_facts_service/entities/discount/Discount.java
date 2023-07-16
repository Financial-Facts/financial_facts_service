package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyBVPS;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyPE;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyROIC;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.TfyPriceData;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.TtmPriceData;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.TtyPriceData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

import static com.facts.financial_facts_service.constants.Constants.CIK_REGEX;


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

    @NotNull
    private Boolean active;

    private Double ratioPrice;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cik")
    private TtmPriceData ttmPriceData;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cik")
    private TfyPriceData tfyPriceData;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cik")
    private TtyPriceData ttyPriceData;

    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuarterlyBVPS> quarterlyBVPS;

    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuarterlyPE> quarterlyPE;

    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuarterlyEPS> quarterlyEPS;

    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuarterlyROIC> quarterlyROIC;

}
