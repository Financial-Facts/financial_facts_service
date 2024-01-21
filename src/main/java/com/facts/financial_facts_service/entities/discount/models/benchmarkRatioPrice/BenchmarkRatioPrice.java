package com.facts.financial_facts_service.entities.discount.models.benchmarkRatioPrice;

import com.facts.financial_facts_service.entities.discount.interfaces.Copyable;
import com.facts.financial_facts_service.entities.discount.interfaces.Valuation;
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
@Table(name = "benchmark_ratio_price")
public class BenchmarkRatioPrice extends Valuation<BenchmarkRatioPrice, BenchmarkRatioPriceInput> {
}
