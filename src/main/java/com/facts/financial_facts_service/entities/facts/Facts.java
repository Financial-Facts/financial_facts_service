package com.facts.financial_facts_service.entities.facts;

import com.facts.financial_facts_service.entities.facts.converter.FactsDataConverter;
import com.facts.financial_facts_service.entities.facts.models.FactsWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Type;

import java.time.LocalDate;

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

}
