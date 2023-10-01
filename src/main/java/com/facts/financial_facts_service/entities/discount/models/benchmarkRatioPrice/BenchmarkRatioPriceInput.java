package com.facts.financial_facts_service.entities.discount.models.benchmarkRatioPrice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "benchmark_ratio_price_input")
@JsonIgnoreProperties(value = { "cik" }, allowSetters = true)
public class BenchmarkRatioPriceInput {

    @Id
    private String cik;

    private String industry;

    private Long ttmRevenue;

    private Long sharesOutstanding;

    private Double psBenchmarkRatio;

}
