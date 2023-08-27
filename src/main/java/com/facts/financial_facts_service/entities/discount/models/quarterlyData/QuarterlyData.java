package com.facts.financial_facts_service.entities.discount.models.quarterlyData;

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
@IdClass(QuarterlyDataKey.class)
@ToString
public class QuarterlyData {

    @Id
    private String cik;

    @Id
    private LocalDate announcedDate;

    private BigDecimal value;

}
