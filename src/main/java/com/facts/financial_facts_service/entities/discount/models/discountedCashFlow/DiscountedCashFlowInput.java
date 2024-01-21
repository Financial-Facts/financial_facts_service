package com.facts.financial_facts_service.entities.discount.models.discountedCashFlow;

import com.facts.financial_facts_service.entities.discount.interfaces.Copyable;
import com.facts.financial_facts_service.entities.discount.models.PeriodicData;
import com.facts.financial_facts_service.entities.discount.models.discountedCashFlow.types.FreeCashFlowHistorical;
import com.facts.financial_facts_service.entities.discount.models.discountedCashFlow.types.FreeCashFlowProjected;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name = "discounted_cash_flow_input")
@JsonIgnoreProperties(value = { "cik" }, allowSetters = true)
public class DiscountedCashFlowInput implements Copyable<DiscountedCashFlowInput> {

    @Id
    private String cik;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FreeCashFlowHistorical> freeCashFlowHistorical;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FreeCashFlowProjected> freeCashFlowProjected;

    private Double wacc;

    private Double riskFreeRate;

    private Long totalCash;

    private Long totalDebt;

    private Long dilutedSharesOutstanding;

    private BigDecimal terminalValue;

    private BigDecimal enterpriseValue;

    @Override
    public void copy(DiscountedCashFlowInput update) {
        this.cik = update.getCik();
        replacePeriodicData(update);
        this.wacc = update.getWacc();
        this.riskFreeRate = update.getRiskFreeRate();
        this.totalCash = update.getTotalCash();
        this.totalDebt = update.getTotalDebt();
        this.dilutedSharesOutstanding = update.getDilutedSharesOutstanding();
        this.terminalValue = update.getTerminalValue();
        this.enterpriseValue = update.getEnterpriseValue();
    }

    private void replacePeriodicData(DiscountedCashFlowInput update) {
        updatePeriodicData(this.freeCashFlowHistorical, update.getFreeCashFlowHistorical());
        updatePeriodicData(this.freeCashFlowProjected, update.getFreeCashFlowProjected());
    }

    private <T extends PeriodicData> void updatePeriodicData(List<T> current, List<T> update) {
        current.clear();
        current.addAll(update);
    }
}
