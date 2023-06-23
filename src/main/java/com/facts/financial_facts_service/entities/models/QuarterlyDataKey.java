package com.facts.financial_facts_service.entities.models;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class QuarterlyDataKey implements Serializable {

    private String cik;

    private LocalDate announcedDate;

}
