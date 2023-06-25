package com.facts.financial_facts_service.entities.facts.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Period {

    private String fp;
    private Integer fy;
    private LocalDate end;
    private BigDecimal val;
    private LocalDate filed;
    private LocalDate start;
    private String frame;

}
