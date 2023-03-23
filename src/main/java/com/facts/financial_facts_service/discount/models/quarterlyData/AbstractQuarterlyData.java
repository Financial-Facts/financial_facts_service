package com.facts.financial_facts_service.discount.models.quarterlyData;

import com.facts.financial_facts_service.discount.Discount;
import jakarta.persistence.*;


import java.time.LocalDate;

@MappedSuperclass
public abstract class AbstractQuarterlyData {

    @Id
    private String cik;

    @ManyToOne()
    @MapsId
    @JoinColumn(name = "cik")
    private Discount discount;

    private LocalDate announcedDate;

    private Double value;

}
