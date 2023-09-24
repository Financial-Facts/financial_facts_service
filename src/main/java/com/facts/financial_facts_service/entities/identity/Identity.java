package com.facts.financial_facts_service.entities.identity;

import com.facts.financial_facts_service.constants.interfaces.Constants;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Identity {

    @NonNull
    String cik;

    @NonNull
    @NotBlank
    String symbol;

    @NonNull
    @NotBlank
    String name;
}
