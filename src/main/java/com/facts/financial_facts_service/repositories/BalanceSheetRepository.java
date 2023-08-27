package com.facts.financial_facts_service.repositories;

import com.facts.financial_facts_service.constants.interfaces.Queries;
import com.facts.financial_facts_service.entities.balanceSheet.BalanceSheet;
import com.facts.financial_facts_service.entities.balanceSheet.models.BalanceSheetKey;
import com.facts.financial_facts_service.repositories.projections.StatementKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BalanceSheetRepository extends JpaRepository<BalanceSheet, BalanceSheetKey>, Queries {

    List<BalanceSheet> findAllByCik(String cik);

    @Query(value = getAllBalanceSheetKeysForCik, nativeQuery = true)
    Set<StatementKey> getAllBalanceSheetKeysForCik(String cik);

}
