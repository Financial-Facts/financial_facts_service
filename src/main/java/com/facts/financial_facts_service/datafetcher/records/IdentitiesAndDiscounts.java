package com.facts.financial_facts_service.datafetcher.records;

import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.entities.identity.Identity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record IdentitiesAndDiscounts(List<Identity> identities,
                                     List<Discount> discounts) {

    public IdentitiesAndDiscounts(List<Identity> identities) {
        this(identities, null);
    }
}
