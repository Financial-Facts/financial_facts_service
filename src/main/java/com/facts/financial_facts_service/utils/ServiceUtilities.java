package com.facts.financial_facts_service.utils;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyFactsEPS;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyLongTermDebt;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyNetIncome;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyOutstandingShares;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.QuarterlyShareholderEquity;
import com.facts.financial_facts_service.entities.models.QuarterlyData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
