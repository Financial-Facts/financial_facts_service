package com.facts.financial_facts_service.entities.serverResponse;

public class FactsResponse extends ServerResponse {

    private String facts;

    public FactsResponse (String message, int status, String facts) {
        super(message, status);
        this.facts = facts;
    }
}
