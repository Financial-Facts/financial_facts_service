package com.facts.financial_facts_service.entities.cikMapping;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(schema = "financial_facts")
public class CikMapping {

    @Id
    String cik;

    String symbol;

    String name;

}
