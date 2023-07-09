package com.facts.financial_facts_service.exceptions;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.ModelType;
import lombok.Getter;

@Getter
public class DataNotFoundException extends RuntimeException implements Constants {

    private String message;

    public DataNotFoundException(String message) {
        this.message = message;
    }

    public DataNotFoundException(ModelType type, String cik) {
        switch(type) {
            case DISCOUNT -> this.message = String.format(DISCOUNT_NOT_FOUND, cik);
            case FACTS -> this.message = String.format(FACTS_NOT_FOUND, cik);
            case IDENTITY -> this.message = String.format(IDENTITY_NOT_FOUND, cik);
            default -> this.message = String.format(DATA_NOT_FOUND, cik);
        }
    }
}
