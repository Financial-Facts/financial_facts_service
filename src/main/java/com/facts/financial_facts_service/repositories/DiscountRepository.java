package com.facts.financial_facts_service.repositories;

import com.facts.financial_facts_service.constants.Queries;
import com.facts.financial_facts_service.datafetcher.projections.SimpleDiscount;
import com.facts.financial_facts_service.entities.discount.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, String>, Queries {

    @Query(value = getAllSimpleDiscounts, nativeQuery = true)
    List<SimpleDiscount> findAllSimpleDiscounts();

}
