package com.facts.financial_facts_service.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CikUtils {

    public static String padSimpleCik(String simpleCik) {
        StringBuilder result = new StringBuilder();
        result.append("CIK").append(simpleCik);
        while (result.length() != 13) {
            result.replace(3, 3, "0");
        }
        return result.toString();
    }
}
