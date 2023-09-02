package com.facts.financial_facts_service.services.api;

import com.facts.financial_facts_service.constants.interfaces.Constants;
import com.facts.financial_facts_service.entities.statements.Statement;
import com.facts.financial_facts_service.entities.statements.models.BalanceSheet;
import com.facts.financial_facts_service.entities.statements.models.IncomeStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class ApiService implements Constants {

    final Logger logger = LoggerFactory.getLogger(ApiService.class);

    @Value("${api.service.api.key}")
    private String apiKey;

    @Autowired
    private WebClient apiWebClient;

    private final Comparator<Statement> comparatorAsc = Comparator.comparing(Statement::getDate);


    public Mono<List<BalanceSheet>> getBalanceSheets(String cik) {
        logger.info("In ApiService getting balance sheets for {}", cik);
        return apiWebClient.get()
            .uri(buildUri(cik, "balance-sheet-statement"))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<BalanceSheet>>() {})
            .flatMap(balanceSheets -> {
                balanceSheets.sort(comparatorAsc);
                return Mono.just(filterUnsupportedCurrency(balanceSheets));
            });
    }

    public Mono<List<IncomeStatement>> getIncomeStatements(String cik) {
        logger.info("In ApiService getting income statements for {}", cik);
        return apiWebClient.get()
            .uri(buildUri(cik, "income-statement"))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<IncomeStatement>>() {})
            .flatMap(incomeStatements -> {
                incomeStatements.sort(comparatorAsc);
                return Mono.just(filterUnsupportedCurrency(incomeStatements));
            });
    }

    private String buildUri(String cik, String identifier) {
        return new StringBuilder()
            .append(SLASH)
            .append(identifier)
            .append(SLASH)
            .append(simplifyCIK(cik))
            .append("?period=quarter")
            .append("&apikey=" + apiKey)
            .append("&limit=120").toString();
    }

    private String simplifyCIK(String cik) {
        return cik.substring(3);
    }

    private <T extends Statement> List<T> filterUnsupportedCurrency(List<T> statements) {
        return statements.stream().filter(statement -> Objects.equals(statement.getReportedCurrency(), "USD")).toList();
    }

}
