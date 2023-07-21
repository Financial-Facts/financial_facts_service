package com.facts.financial_facts_service.services.identity.comparators;

import com.amazonaws.util.StringUtils;
import com.facts.financial_facts_service.entities.identity.Identity;
import org.springframework.stereotype.Component;

import java.util.Comparator;

public class IdentityCikComparator implements Comparator<Identity> {
    @Override
    public int compare(Identity o1, Identity o2) {
        return StringUtils.compare(o1.getCik(), o2.getCik());
    }
}
