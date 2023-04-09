package com.facts.financial_facts_service.utils;


import com.facts.financial_facts_service.constants.Constants;

public class CikUtils {

    public static String padSimpleCik(String simpleCik) {
        StringBuilder result = new StringBuilder();
        result.append(Constants.CIK).append(simpleCik);
        while (result.length() != 13) {
            result.replace(3, 3, Constants.ZERO);
        }
        return result.toString();
    }
}
