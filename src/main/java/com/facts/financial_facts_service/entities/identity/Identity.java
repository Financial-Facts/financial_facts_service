package com.facts.financial_facts_service.entities.identity;

import com.facts.financial_facts_service.constants.interfaces.Constants;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(schema = Constants.FINANCIAL_FACTS)
public class Identity {

    @Id
    @NonNull
    @JsonAlias(value = "cik_str")
    String cik;

    @NonNull
    @NotBlank
    @JsonAlias(value = "ticker")
    String symbol;

    @NonNull
    @NotBlank
    @JsonAlias(value = "title")
    String name;
}
