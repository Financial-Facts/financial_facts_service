package com.facts.financial_facts_service.entities.discount.models.discountedCashFlow;

import com.facts.financial_facts_service.entities.discount.interfaces.Copyable;
import com.facts.financial_facts_service.entities.discount.models.PeriodicData;
import com.facts.financial_facts_service.entities.discount.models.discountedCashFlow.types.*;
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

    private String symbol;

    private Double longTermGrowthRate;

    private BigDecimal freeCashFlowT1;

    private Double wacc;

    private BigDecimal terminalValue;

    private BigDecimal enterpriseValue;

    private Long netDebt;

    private Long dilutedSharesOutstanding;

    private Long marketPrice;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricalRevenue> historicalRevenue;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectedRevenue> projectedRevenue;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricalOperatingCashFlow> historicalOperatingCashFlow;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectedOperatingCashFlow> projectedOperatingCashFlow;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricalCapitalExpenditure> historicalCapitalExpenditure;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectedCapitalExpenditure> projectedCapitalExpenditure;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricalFreeCashFlow> historicalFreeCashFlow;

    @OrderBy("announced_date ASC")
    @OneToMany(mappedBy = "cik", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectedFreeCashFlow> projectedFreeCashFlow;

    @Override
    public void copy(DiscountedCashFlowInput update) {
        this.setCik(update.getCik());
        this.setSymbol(update.getSymbol());
        this.setLongTermGrowthRate(update.getLongTermGrowthRate());
        this.setFreeCashFlowT1(update.getFreeCashFlowT1());
        this.setWacc(update.getWacc());
        this.setTerminalValue(update.getTerminalValue());
        this.setEnterpriseValue(update.getEnterpriseValue());
        this.setNetDebt(update.getNetDebt());
        this.setDilutedSharesOutstanding(update.getDilutedSharesOutstanding());
        this.setMarketPrice(update.getMarketPrice());
        this.replacePeriodicData(update);
    }

    private void replacePeriodicData(DiscountedCashFlowInput update) {
        updatePeriodicData(this.getHistoricalRevenue(), update.getHistoricalRevenue());
        updatePeriodicData(this.getProjectedRevenue(), update.getProjectedRevenue());
        updatePeriodicData(this.getHistoricalOperatingCashFlow(), update.getHistoricalOperatingCashFlow());
        updatePeriodicData(this.getProjectedOperatingCashFlow(), update.getProjectedOperatingCashFlow());
        updatePeriodicData(this.getHistoricalCapitalExpenditure(), update.getHistoricalCapitalExpenditure());
        updatePeriodicData(this.getProjectedCapitalExpenditure(), update.getProjectedCapitalExpenditure());
        updatePeriodicData(this.getHistoricalFreeCashFlow(), update.getHistoricalFreeCashFlow());
        updatePeriodicData(this.getProjectedFreeCashFlow(), update.getProjectedFreeCashFlow());
    }

    private <T extends PeriodicData> void updatePeriodicData(List<T> current, List<T> update) {
        current.clear();
        current.addAll(update);
    }
}
