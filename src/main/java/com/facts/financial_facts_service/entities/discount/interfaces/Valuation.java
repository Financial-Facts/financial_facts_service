package com.facts.financial_facts_service.entities.discount.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@JsonIgnoreProperties(value = { "cik" }, allowSetters = true)
public abstract class Valuation<T, U extends Copyable<U>> implements Copyable<Valuation<T, U>> {

    @Id
    String cik;

    Double price;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cik")
    U input;

    @Override
    public void copy(Valuation<T, U> update) {
        this.cik = update.getCik();
        this.price = update.getPrice();
        this.input.copy(update.getInput());
    }

}
