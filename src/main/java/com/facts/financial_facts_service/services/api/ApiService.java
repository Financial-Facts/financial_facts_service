package com.facts.financial_facts_service.services.api;

import com.facts.financial_facts_service.constants.interfaces.Constants;
import com.facts.financial_facts_service.entities.balanceSheet.BalanceSheet;
import com.facts.financial_facts_service.entities.incomeStatement.IncomeStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ApiService implements Constants {

    final Logger logger = LoggerFactory.getLogger(ApiService.class);

    @Value("${api.service.api.key}")
    private String apiKey;

    @Autowired
    private WebClient apiWebClient;

    public Mono<List<BalanceSheet>> getBalanceSheets(String cik) {
        logger.info("In ApiService getting balance sheets for {}", cik);
        return apiWebClient.get()
            .uri(buildUri(cik, "balance-sheet-statement"))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<BalanceSheet>>() {});
    }

    public Mono<List<IncomeStatement>> getIncomeStatements(String cik) {
        logger.info("In ApiService getting income statements for {}", cik);
        return apiWebClient.get()
            .uri(buildUri(cik, "income-statement"))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<IncomeStatement>>() {});
    }

    private String buildUri(String cik, String identifier) {
        return new StringBuilder()
            .append(SLASH)
            .append(identifier)
            .append(SLASH)
            .append(simplifyCIK(cik))
            .append("?period=quarter")
            .append("&apikey=" + apiKey)
            .append("&limit=40").toString();
    }

    private String simplifyCIK(String cik) {
        return cik.substring(3);
    }

}
