package com.facts.financial_facts_service.entities.facts;

import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyBVPS;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import com.facts.financial_facts_service.entities.facts.retriever.models.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.entities.facts.retriever.models.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.retriever.models.QuarterlyShareholderEquity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.Date;
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
    private String cik;

    @NonNull
    private LocalDate lastSync;

    @NonNull
    @NotBlank
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    @JsonIgnore
    private String data;

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
}
