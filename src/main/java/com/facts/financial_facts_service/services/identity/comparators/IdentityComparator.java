package com.facts.financial_facts_service.services.identity.comparators;

import com.amazonaws.util.StringUtils;
import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.entities.identity.models.SortBy;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.Comparator;

@AllArgsConstructor
public class IdentityComparator implements Comparator<Identity> {

    private SortBy sortBy;

    private boolean reversed;

    @Override
    public int compare(Identity o1, Identity o2) {
        String a = getValue(o1);
        String b = getValue(o2);

        if (a.length() != b.length()) {
            return a.length() - b.length();
        }
        return reversed ? StringUtils.compare(b, a) : StringUtils.compare(a, b);
    }

    private String getValue(Identity identity) {
        return switch(sortBy) {
            case CIK -> identity.getCik();
            case SYMBOL -> identity.getSymbol();
            case NAME -> identity.getName();
        };
    }
}
