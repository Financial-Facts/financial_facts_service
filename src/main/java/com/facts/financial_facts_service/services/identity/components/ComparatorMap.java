package com.facts.financial_facts_service.services.identity.components;

import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.entities.identity.comparators.IdentityCikComparator;
import com.facts.financial_facts_service.entities.identity.comparators.IdentityNameComparator;
import com.facts.financial_facts_service.entities.identity.comparators.IdentitySymbolComparator;
import com.facts.financial_facts_service.entities.identity.models.SortBy;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ComparatorMap {

    @Autowired
    private IdentityCikComparator cikComparator;

    @Autowired
    private IdentityNameComparator nameComparator;

    @Autowired
    private IdentitySymbolComparator symbolComparator;

    private ConcurrentHashMap<SortBy, Comparator<Identity>> comparatorMap;

    @PostConstruct
    private void init() {
        comparatorMap = new ConcurrentHashMap<>();
        comparatorMap.put(SortBy.CIK, cikComparator);
        comparatorMap.put(SortBy.NAME, nameComparator);
        comparatorMap.put(SortBy.SYMBOL, symbolComparator);
    }

    public Comparator<Identity> getComparator(SortBy sortBy) {
        return comparatorMap.get(sortBy);
    }
}
