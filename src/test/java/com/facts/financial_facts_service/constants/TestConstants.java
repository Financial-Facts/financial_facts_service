package com.facts.financial_facts_service.constants;

import java.util.List;

public interface TestConstants {
    String LOWERCASE_CIK = "cik0123456789";
    String CIK = "CIK0123456789";
    String CIK2 = "CIK1111122222";
    String SYMBOL = "ABC";
    String NAME = "Company";
    String SUCCESS = "Success";
    String FACTS = "Facts JSON";
    String EMPTY = "";
    String CIK_PATH_PARAM = "/{cik}";
    String DISCOUNT_NOT_FOUND_TEST = "Discount with cik CIK0123456789 not found";
    String SEC_URL = "https://www.sec.com/test";
    String USER_AGENT = "user@agent.com";
    String FACTS_URL = "https://www.facts.com/test";
    String INVALID_CIK = "invalidCik";
    String DISCOUNT_OPERATION_ERROR = "Error occurred performing %s operation on CIK %s";
    String DISCOUNT_OPERATION_ERROR_NO_CIK = "Error occurred performing %s operation";
    String USD = "USD";
    String DOGE_COIN = "DOGE";
    String SHARES = "shares";
    String FACTS_KEY_1 = "factsKey1";
    String FACTS_KEY_2 = "factsKey2";
    List<String> FACTS_KEYS = List.of(FACTS_KEY_1, FACTS_KEY_2);
    String SHAREHOLDER_EQUITY = "shareholderEquity";
    String OUTSTANDING_SHARES = "outstandingShares";
    String EPS = "EPS";
    String LONG_TERM_DEBT = "longTermDebt";
    String NET_INCOME = "netIncome";
}
