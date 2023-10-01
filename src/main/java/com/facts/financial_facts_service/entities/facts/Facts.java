package com.facts.financial_facts_service.entities.facts;

import com.facts.financial_facts_service.entities.facts.converter.FactsDataConverter;
import com.facts.financial_facts_service.entities.facts.models.FactsWrapper;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Facts {

    @Id
    @NonNull
    private String cik;

    @NonNull
    private LocalDate lastSync;

    @NonNull
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    @Convert(converter = FactsDataConverter.class)
    private FactsWrapper data;

}
