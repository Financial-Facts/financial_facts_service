package com.facts.financial_facts_service.services.identity.comparators;

import com.amazonaws.util.StringUtils;
import com.facts.financial_facts_service.entities.identity.Identity;

import java.util.Comparator;

public class IdentitySymbolComparator implements Comparator<Identity> {
    @Override
    public int compare(Identity o1, Identity o2) {
        return StringUtils.compare(o1.getSymbol(), o2.getSymbol());
    }
}
