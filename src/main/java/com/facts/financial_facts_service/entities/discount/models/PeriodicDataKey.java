package com.facts.financial_facts_service.entities.discount.models;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PeriodicDataKey {

    private String cik;

    private LocalDate announcedDate;

}
