package com.facts.financial_facts_service.constants.enums;

public enum Taxonomy {

    US_GAAP,
    IFRS_FULL,
    DEI;

    @Override
    public String toString() {
        return switch (this) {
            case US_GAAP -> "us-gaap";
            case IFRS_FULL -> "ifrs-full";
            case DEI -> "dei";
            default -> null;
        };
    }

}
