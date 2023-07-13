package com.facts.financial_facts_service.entities.identity.models;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BulkIdentitiesRequest {

    @PositiveOrZero(message = "Start index must be 0 or greater")
    private int startIndex;

    @PositiveOrZero(message = "Limit index must be 0 or greater")
    private int limit;

    private SortBy sortBy;

    private SortOrder order;

}
