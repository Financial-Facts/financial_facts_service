package com.facts.financial_facts_service.services.facts.components.retriever;

import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.*;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface IRetriever {

    Mono<List<?>> retrieveStickerPriceData(String cik, TaxonomyReports taxonomyReports);

    <T extends AbstractQuarterlyData> Mono<List<T>> retrieveQuarterlyData(String cik, TaxonomyReports taxonomyReports, Class<T> type);

}
