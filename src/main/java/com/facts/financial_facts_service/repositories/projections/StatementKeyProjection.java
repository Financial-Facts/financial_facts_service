package com.facts.financial_facts_service.repositories.projections;

import java.time.LocalDate;

public interface StatementKeyProjection {

    String getCik();

    LocalDate getDate();

}
