package com.facts.financial_facts_service.repositories;

import com.facts.financial_facts_service.constants.interfaces.Queries;
import com.facts.financial_facts_service.entities.incomeStatement.IncomeStatement;
import com.facts.financial_facts_service.entities.incomeStatement.models.IncomeStatementKey;
import com.facts.financial_facts_service.repositories.projections.StatementKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface IncomeStatementRepository extends JpaRepository<IncomeStatement, IncomeStatementKey>, Queries {

    List<IncomeStatement> findAllByCik(String cik);

    @Query(value = getAllIncomeStatementKeysForCik, nativeQuery = true)
    Set<StatementKey> getAllIncomeStatementKeysForCik(String cik);

}
