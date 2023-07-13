package com.facts.financial_facts_service.services.facts.components.retriever;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public abstract class AbstractRetriever implements IRetriever, Constants {

    public Mono<List<?>> fetchQuarterlyData(Set<String> params, String cik, TaxonomyReports taxonomyReports) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (String param: params) {
            switch (param) {
                case SHAREHOLDER_EQUITY: {
                    futures.add(retrieve_quarterly_shareholder_equity(cik, taxonomyReports).toFuture());
                    break;
                }
                case OUTSTANDING_SHARES: {
                    futures.add(retrieve_quarterly_outstanding_shares(cik, taxonomyReports).toFuture());
                    break;
                }
                case EPS: {
                    futures.add(retrieve_quarterly_EPS(cik, taxonomyReports).toFuture());
                    break;
                }
                case LONG_TERM_DEBT: {
                    futures.add(retrieve_quarterly_long_term_debt(cik, taxonomyReports).toFuture());
                    break;
                }
                case NET_INCOME: {
                    futures.add(retrieve_quarterly_net_income(cik, taxonomyReports).toFuture());
                }
                default: {
                    break;
                }
            }
        }
        return Mono.fromFuture(CompletableFuture.supplyAsync(() ->
            futures.stream().map(future -> {
                try {
                    return future.get();
                } catch (InterruptedException | ExecutionException ex) {
                    return Collections.emptyList();
                }
            }).collect(Collectors.toList())));
    }

}
