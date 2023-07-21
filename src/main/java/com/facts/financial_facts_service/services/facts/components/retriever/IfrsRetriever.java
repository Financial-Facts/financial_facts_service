package com.facts.financial_facts_service.services.facts.components.retriever;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
public class IfrsRetriever extends AbstractRetriever implements IRetriever {

    @PostConstruct
    private void init() {
        keyMap = taxonomyKeyMap.getIfrs();
    }
}