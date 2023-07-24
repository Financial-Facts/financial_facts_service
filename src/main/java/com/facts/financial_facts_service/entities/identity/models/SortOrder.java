package com.facts.financial_facts_service.entities.identity.models;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum SortOrder {

    ASC,
    DESC;

    public SortOrder from(String val) {
        switch (val.toUpperCase()) {
            case ("ASC") -> {
                return ASC;
            }
            case ("DESC") -> {
                return DESC;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sortBy");
    }
}
