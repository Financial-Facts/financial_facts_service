package com.facts.financial_facts_service.entities.facts.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaxonomyReports implements Serializable {

    @JsonAlias("us-gaap")
    private Map<String, UnitData> gaap;

    @JsonAlias("ifrs-full")
    private Map<String, UnitData>  ifrs;

    private Map<String, UnitData> dei;

}
