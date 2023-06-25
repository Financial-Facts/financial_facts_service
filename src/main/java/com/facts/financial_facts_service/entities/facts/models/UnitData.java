package com.facts.financial_facts_service.entities.facts.parser.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class UnitData implements Serializable {

    private String label;
    private Map<String, List<Period>> units;
    private String description;

}
