package com.facts.financial_facts_service.services.facts.components.retriever;

import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.services.facts.components.retriever.models.StickerPriceQuarterlyData;
import reactor.core.publisher.Mono;


public interface IRetriever {

    Mono<StickerPriceQuarterlyData> retrieveStickerPriceData(String cik, TaxonomyReports taxonomyReports);
}
