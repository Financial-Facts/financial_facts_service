package com.facts.financial_facts_service.utils;

import com.facts.financial_facts_service.constants.interfaces.Constants;

public class ServiceUtilities implements Constants {

    public static String padSimpleCik(String simpleCik) {
        StringBuilder result = new StringBuilder();
        result.append(CIK).append(simpleCik);
        if (result.length() > 13) {
            result.replace(13, result.length(), EMPTY);
        }
        while (result.length() < 13) {
            result.replace(3, 3, ZERO);
        }
        return result.toString();
    }
}
