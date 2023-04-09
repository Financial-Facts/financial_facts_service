package com.facts.financial_facts_service.entities.serverResponse;

import com.facts.financial_facts_service.entities.identity.Identity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdentityResponse extends ServerResponse {

    private Identity identity;

    public IdentityResponse (String message, int status, Identity identity) {
        super(message, status);
        this.identity = identity;
    }

}
