package com.facts.financial_facts_service.entities.facts.parser.models;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

@Getter
@Setter
public class UnitData {

    private String label;
    private JSONObject units;
    private String description;

}
