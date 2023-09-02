package com.facts.financial_facts_service.entities.statements;

import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyDataKey;
import com.facts.financial_facts_service.entities.statements.models.StatementKey;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

import static com.facts.financial_facts_service.constants.interfaces.Constants.CIK_REGEX;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode
@IdClass(StatementKey.class)
@ToString
public abstract class Statement {

    @Id
    @Pattern(regexp = CIK_REGEX)
    private String cik;

    @Id
    private LocalDate date;

    private String symbol;
    private String reportedCurrency;
    private LocalDate fillingDate;
    private String acceptedDate;
    private String calendarYear;
    private String period;

}
