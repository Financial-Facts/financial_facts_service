package com.facts.financial_facts_service.entities.facts.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FactsResponseWrapper implements Serializable {

    private String cik;
    private String entityName;

    @JsonAlias("facts")
    private TaxonomyReports taxonomyReports;

}
