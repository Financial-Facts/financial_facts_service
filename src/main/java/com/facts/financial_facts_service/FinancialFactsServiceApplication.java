package com.facts.financial_facts_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class FinancialFactsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancialFactsServiceApplication.class, args);
	}

}
