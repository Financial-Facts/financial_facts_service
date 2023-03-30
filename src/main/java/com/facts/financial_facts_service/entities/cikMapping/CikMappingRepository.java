package com.facts.financial_facts_service.entities.cikMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CikMappingRepository extends JpaRepository<CikMapping, String> {
}
