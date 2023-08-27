package com.facts.financial_facts_service.repositories.projections;

import java.time.LocalDate;

public interface StatementKey {

    String getCik();

    LocalDate getDate();

}
