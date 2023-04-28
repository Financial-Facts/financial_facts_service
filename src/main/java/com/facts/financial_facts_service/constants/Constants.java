package com.facts.financial_facts_service.constants;

public interface Constants {
    String V1_DISCOUNT = "v1/discount";
    String CIK_PATH_PARAM = "/{cik}";
    String INVALID_INPUT = "Invalid input parameters";
    String DISCOUNT_NOT_FOUND = "Discount with cik %s not found";
    String FACTS_NOT_FOUND = "Facts with cik %s not found";
    String IDENTITY_NOT_FOUND = "Identity with cik %s not found";
    String DATA_NOT_FOUND = "Data not found for cik %s";
    String DISCOUNT_EXISTS = "Discount with cik %s already exists";
    String DISCOUNT_OPERATION_ERROR = "Error occurred performing %s operation on CIK %s";
    String ADD = "ADD";
    String UPDATE = "UPDATE";
    String V1_FACTS = "v1/facts";
    String V1_IDENTITY = "v1/identity";
    String IDENTITY_OPERATION_ERROR = "Error occurred performing GET operation on CIK %s";
    String CIK = "CIK";
    String ZERO = "0";
    String FINANCIAL_FACTS = "financial_facts";
    String SUCCESS = "Success";
    String CIK_REGEX = "^CIK\\d{10}$";
}
