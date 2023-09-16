package com.facts.financial_facts_service.repositories;

import com.facts.financial_facts_service.constants.interfaces.Queries;
import com.facts.financial_facts_service.entities.statements.models.CashFlowStatement;
import com.facts.financial_facts_service.entities.statements.models.StatementKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CashFlowStatementRepository extends JpaRepository<CashFlowStatement, StatementKey>, Queries {

    List<CashFlowStatement> findAllByCikOrderByDateAsc(String cik);

}
