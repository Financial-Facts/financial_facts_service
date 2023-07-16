package com.facts.financial_facts_service.entities.discount.models;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Map;

import static com.facts.financial_facts_service.constants.Constants.CIK_REGEX;

@Getter
@Setter
public class UpdateDiscountInput {

    Map<@Pattern(regexp = CIK_REGEX) String, Boolean> discountUpdateMap;

}
