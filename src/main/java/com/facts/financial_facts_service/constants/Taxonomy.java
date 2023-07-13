package com.facts.financial_facts_service.constants;

public enum Taxonomy {

    US_GAAP,
    IFRS_FULL,
    DEI;

    @Override
    public String toString() {
        switch (this) {
            case US_GAAP:
                return "us-gaap";
            case IFRS_FULL:
                return "ifrs-full";
            case DEI:
                return "dei";
            default:
                return null;
        }
    }

}
