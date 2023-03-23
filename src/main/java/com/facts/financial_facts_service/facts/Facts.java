package com.facts.financial_facts_service.facts;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.Type;

@Data
@Entity
public class Facts {

    @Id
    private String cik;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private String data;

}
