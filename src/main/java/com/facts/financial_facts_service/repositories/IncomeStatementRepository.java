package com.facts.financial_facts_service.repositories;

import com.facts.financial_facts_service.constants.interfaces.Queries;
import com.facts.financial_facts_service.entities.statements.models.IncomeStatement;
import com.facts.financial_facts_service.entities.statements.models.StatementKey;
import com.facts.financial_facts_service.repositories.projections.StatementKeyProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface IncomeStatementRepository extends JpaRepository<IncomeStatement, StatementKey>, Queries {

    List<IncomeStatement> findAllByCikOrderByDateAsc(String cik);

    @Query(value = getAllIncomeStatementKeysForCik, nativeQuery = true)
    Set<StatementKeyProjection> getAllIncomeStatementKeysForCik(String cik);

}
