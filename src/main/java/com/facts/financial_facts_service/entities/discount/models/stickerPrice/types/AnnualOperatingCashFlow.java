package com.facts.financial_facts_service.entities.discount.models.stickerPrice.types;

import com.facts.financial_facts_service.entities.discount.models.PeriodicData;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "annual_operating_cash_flow")
public class AnnualOperatingCashFlow extends PeriodicData {
}
