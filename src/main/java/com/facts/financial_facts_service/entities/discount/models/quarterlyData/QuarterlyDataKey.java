package com.facts.financial_facts_service.entities.discount.models.quarterlyData;

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
