package com.facts.financial_facts_service.entities.identity.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BulkIdentitiesRequest {

    @PositiveOrZero(message = "Start index must be 0 or greater")
    private int startIndex;

    @PositiveOrZero(message = "Limit index must be 0 or greater")
    private int limit;

    private String keyword;

    private SearchBy searchBy;

    private SortBy sortBy;

    private SortOrder order;

}
