package com.facts.financial_facts_service.entities.models;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class QuarterlyDataKey {

    private String cik;

    private LocalDate announcedDate;

}
