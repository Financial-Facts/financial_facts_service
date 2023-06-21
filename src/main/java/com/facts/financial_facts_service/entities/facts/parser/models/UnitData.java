package com.facts.financial_facts_service.entities.facts.parser.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class UnitData {

    private String label;
    private Map<String, List<Period>> units;
    private String description;

}
