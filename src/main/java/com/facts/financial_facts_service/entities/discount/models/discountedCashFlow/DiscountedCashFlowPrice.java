package com.facts.financial_facts_service.entities.discount.models.discountedCashFlow;

import com.facts.financial_facts_service.entities.discount.interfaces.Valuation;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name = "discounted_cash_flow_price")
public class DiscountedCashFlowPrice extends Valuation<DiscountedCashFlowPrice, DiscountedCashFlowInput> {
}
