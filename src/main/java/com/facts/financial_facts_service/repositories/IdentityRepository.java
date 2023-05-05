package com.facts.financial_facts_service.repositories;

import com.facts.financial_facts_service.entities.identity.Identity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentityRepository extends JpaRepository<Identity, String> {
}
