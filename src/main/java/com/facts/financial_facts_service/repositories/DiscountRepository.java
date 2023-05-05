package com.facts.financial_facts_service.repositories;

import com.facts.financial_facts_service.entities.discount.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, String> {
}
