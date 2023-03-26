package com.facts.financial_facts_service.discount;

import com.facts.financial_facts_service.discount.models.quarterlyData.QuarterlyBVPS;
import com.facts.financial_facts_service.discount.models.quarterlyData.QuarterlyEPS;
import com.facts.financial_facts_service.discount.models.quarterlyData.QuarterlyPE;
import com.facts.financial_facts_service.discount.models.quarterlyData.QuarterlyROIC;
import com.facts.financial_facts_service.discount.models.trailingPriceData.TfyPriceData;
import com.facts.financial_facts_service.discount.models.trailingPriceData.TtmPriceData;
import com.facts.financial_facts_service.discount.models.trailingPriceData.TtyPriceData;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.time.LocalDate;

import java.util.List;


@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(schema = "financial_facts")
public class Discount {

    @Id
    private String cik;

    private String symbol;

    private String name;

    private Double ratioPrice;

    private LocalDate lastUpdated;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private List<TtmPriceData> ttmPriceData;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private List<TfyPriceData> tfyPriceData;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private List<TtyPriceData> ttyPriceData;

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
