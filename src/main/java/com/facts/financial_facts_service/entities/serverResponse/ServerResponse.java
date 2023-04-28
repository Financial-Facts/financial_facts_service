package com.facts.financial_facts_service.entities.serverResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerResponse {

    private String message;
    private int status;
    private List<String> errors;

    public ServerResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.errors = new ArrayList<>();
    }
}
