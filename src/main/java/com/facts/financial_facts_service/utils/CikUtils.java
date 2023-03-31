package com.facts.financial_facts_service.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CikUtils {

    public static boolean areSameCIK(String paddedCik, int simpleCik) {
        Matcher matcher = Pattern.compile("[1-9]+").matcher(paddedCik);
        matcher.find();
        paddedCik = paddedCik.substring(paddedCik.indexOf(matcher.group()));
        return (simpleCik + "").equals(paddedCik);
    }

    public static String padSimpleCik(int simpleCik) {
        StringBuilder result = new StringBuilder();
        result.append("CIK").append(simpleCik);
        while (result.length() != 13) {
            result.replace(3, 3, "0");
        }
        return result.toString();
    }
}
