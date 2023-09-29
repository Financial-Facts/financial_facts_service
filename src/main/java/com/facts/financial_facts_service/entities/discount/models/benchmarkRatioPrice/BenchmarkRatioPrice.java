package com.facts.financial_facts_service.entities.discount.models.benchmarkRatioPrice;

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
@Table(name = "benchmark_ratio_price")
@JsonIgnoreProperties(value = { "cik" }, allowSetters = true)
public class BenchmarkRatioPrice {

    @Id
    private String cik;

    private Double ratioPrice;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cik")
    private BenchmarkRatioPriceInput input;

}
