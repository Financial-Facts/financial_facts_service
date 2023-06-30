package com.facts.financial_facts_service.entities.models;

import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode
@IdClass(QuarterlyDataKey.class)
public abstract class AbstractQuarterlyData {

    @Id
    private String cik;

    @Id
    private LocalDate announcedDate;

    private BigDecimal value;

}
