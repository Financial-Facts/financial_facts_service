package com.facts.financial_facts_service.entities.facts.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FactsWrapper implements Serializable {

    @JsonIgnore
    private String cik;

    private String entityName;

    @JsonAlias("facts")
    private TaxonomyReports taxonomyReports;

}
