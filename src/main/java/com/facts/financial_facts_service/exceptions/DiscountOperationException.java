package com.facts.financial_facts_service.exceptions;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.discount.models.Operation;

public class DiscountOperationException extends RuntimeException implements Constants {

    public DiscountOperationException(Operation operation, String cik) {
        super(String.format(DISCOUNT_OPERATION_ERROR, operation.toString(), cik));
    }
}
