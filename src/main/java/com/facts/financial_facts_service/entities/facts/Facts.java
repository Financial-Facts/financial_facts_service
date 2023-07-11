package com.facts.financial_facts_service.entities.facts;

import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import com.facts.financial_facts_service.entities.facts.converter.FactsDataConverter;
import com.facts.financial_facts_service.entities.facts.models.FactsWrapper;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.*;
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

    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuarterlyShareholderEquity> quarterlyShareholderEquity;

    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuarterlyOutstandingShares> quarterlyOutstandingShares;

    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuarterlyFactsEPS> quarterlyEPS;

    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuarterlyLongTermDebt> quarterlyLongTermDebt;

    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuarterlyNetIncome> quarterlyNetIncome;

    public Facts(String cik, LocalDate lastSync, FactsWrapper data) {
        this.cik = cik;
        this.lastSync = lastSync;
        this.data = data;
        this.quarterlyShareholderEquity = Collections.emptyList();
        this.quarterlyOutstandingShares = Collections.emptyList();
        this.quarterlyEPS = Collections.emptyList();
        this.quarterlyLongTermDebt = Collections.emptyList();
        this.quarterlyNetIncome = Collections.emptyList();
    }
}
