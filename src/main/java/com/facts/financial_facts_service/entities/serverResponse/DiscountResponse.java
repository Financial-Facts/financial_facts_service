package com.facts.financial_facts_service.entities.serverResponse;

import com.facts.financial_facts_service.entities.discount.Discount;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscountResponse extends ServerResponse {

    private Discount discount;

    public DiscountResponse(String message, int status, Discount discount) {
        super(message, status);
        this.discount = discount;
    }
}
