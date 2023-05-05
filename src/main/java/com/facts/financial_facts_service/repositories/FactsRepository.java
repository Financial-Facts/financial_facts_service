package com.facts.financial_facts_service.repositories;

import com.facts.financial_facts_service.entities.facts.Facts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactsRepository extends JpaRepository<Facts, String> {
}
