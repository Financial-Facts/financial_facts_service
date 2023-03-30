package com.facts.financial_facts_service.entities.facts;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Type;

@Data
@Entity
@Table(schema = "financial_facts")
public class Facts {

    @Id
    private String cik;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private String data;

}
