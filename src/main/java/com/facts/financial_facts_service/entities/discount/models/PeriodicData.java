package com.facts.financial_facts_service.entities.discount.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.math.BigDecimal;
import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode
@IdClass(PeriodicDataKey.class)
@ToString
@JsonIgnoreProperties(value = { "cik" }, allowSetters = true)
public class PeriodicData {

    @Id
    private String cik;

    @Id
    private LocalDate announcedDate;

    private BigDecimal value;

}
