package com.facts.financial_facts_service.entities.cikMapping.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

@Getter
public class Identity {

    private String cik_str;

    @JsonAlias(value = "ticker")
    private String symbol;

    @JsonAlias(value = "title")
    private String name;
}
