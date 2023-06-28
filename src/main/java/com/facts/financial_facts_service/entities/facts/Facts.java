package com.facts.financial_facts_service.entities.facts;

import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import com.facts.financial_facts_service.entities.facts.converter.FactsDataConverter;
import com.facts.financial_facts_service.entities.facts.models.FactsWrapper;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyShareholderEquity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.facts.financial_facts_service.constants.Constants.FINANCIAL_FACTS;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(schema = FINANCIAL_FACTS)
public class Facts {

    @Id
    @NonNull
    @JsonIgnore
    private String cik;

    @NonNull
    private LocalDate lastSync;

    @NonNull
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    @JsonIgnore
    @Convert(converter = FactsDataConverter.class)
    private FactsWrapper data;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private List<QuarterlyShareholderEquity> quarterlyShareholderEquity;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private List<QuarterlyOutstandingShares> quarterlyOutstandingShares;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private List<QuarterlyEPS> quarterlyEPS;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private List<QuarterlyLongTermDebt> quarterlyLongTermDebt;

    public Facts(String cik, LocalDate lastSync, FactsWrapper data) {
        this.cik = cik;
        this.lastSync = lastSync;
        this.data = data;
        this.quarterlyShareholderEquity = Collections.emptyList();
        this.quarterlyOutstandingShares = Collections.emptyList();
        this.quarterlyEPS = Collections.emptyList();
        this.quarterlyLongTermDebt = Collections.emptyList();
    }
}
