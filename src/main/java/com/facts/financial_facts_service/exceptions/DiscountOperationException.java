package com.facts.financial_facts_service.exceptions;

import com.facts.financial_facts_service.constants.interfaces.Constants;
import com.facts.financial_facts_service.constants.enums.Operation;

public class DiscountOperationException extends RuntimeException implements Constants {

    public DiscountOperationException(Operation operation, String cik) {
        super(String.format(DISCOUNT_OPERATION_ERROR, operation.toString(), cik));
    }

    public DiscountOperationException(Operation operation) {
        super(String.format(DISCOUNT_OPERATION_ERROR_NO_CIK, operation.toString()));
    }
}
