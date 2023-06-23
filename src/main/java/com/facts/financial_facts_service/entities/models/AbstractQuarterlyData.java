package com.facts.financial_facts_service.entities.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
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
    @JsonIgnore
    private String cik;

    @Id
    private LocalDate announcedDate;

    private BigDecimal value;

}
