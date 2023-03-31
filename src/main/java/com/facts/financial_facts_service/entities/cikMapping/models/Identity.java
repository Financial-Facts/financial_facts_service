package com.facts.financial_facts_service.entities.cikMapping.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;

@Data
public class Identity {

    @JsonAlias(value = "cik_str")
    private int cik;

    @JsonAlias(value = "ticker")
    private String symbol;

    @JsonAlias(value = "title")
    private String name;
}
