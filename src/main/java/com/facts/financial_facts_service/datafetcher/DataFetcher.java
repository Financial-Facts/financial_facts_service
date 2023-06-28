package com.facts.financial_facts_service.datafetcher;

import com.facts.financial_facts_service.datafetcher.records.FactsData;
import com.facts.financial_facts_service.datafetcher.records.StickerPriceData;
import com.facts.financial_facts_service.services.DiscountService;
import com.facts.financial_facts_service.services.facts.FactsService;
import com.facts.financial_facts_service.services.identity.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DataFetcher {

    @Autowired
    private FactsService factsService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private DiscountService discountService;

    public Mono<FactsData> getFactsWithCik(String cik) {
        return factsService.getFactsWithCik(cik).flatMap(facts -> Mono.just(new FactsData(facts)));
    }

    public Mono<StickerPriceData> getStickerPriceDataWithCik(String cik) {
        return factsService.getFactsWithCik(cik).flatMap(facts ->
            identityService.getIdentityFromIdentityMap(facts.getCik()).flatMap(identity ->
                Mono.just(new StickerPriceData(identity, facts))));
    }


}
