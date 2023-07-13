package com.facts.financial_facts_service.entities.discount.models.trailingPriceData;

import com.facts.financial_facts_service.constants.Constants;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "tty_price_data")
public class TtyPriceData extends AbstractTrailingPriceData {
}
