package com.facts.financial_facts_service.services.facts.components.retriever;

import com.facts.financial_facts_service.constants.Constants;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
public class GaapRetriever extends AbstractRetriever implements IRetriever, Constants {

    @PostConstruct
    private void init() {
        keyMap = taxonomyKeyMap.getGaap();
    }
}
