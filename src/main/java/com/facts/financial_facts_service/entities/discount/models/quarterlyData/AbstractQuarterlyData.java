package com.facts.financial_facts_service.entities.discount.models.quarterlyData;

import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDate;

@MappedSuperclass
@Data
public abstract class AbstractQuarterlyData {

    @Id
    private String cik;

    private LocalDate announcedDate;

    private Double value;

}
