package com.facts.financial_facts_service.entities.facts;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Type;

import static com.facts.financial_facts_service.constants.Constants.FINANCIAL_FACTS;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = FINANCIAL_FACTS)
public class Facts {

    @Id
    @NonNull
    private String cik;

    @NonNull
    @NotBlank
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private String data;

}
