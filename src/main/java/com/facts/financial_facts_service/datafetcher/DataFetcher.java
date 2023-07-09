package com.facts.financial_facts_service.datafetcher;

import com.facts.financial_facts_service.datafetcher.records.FactsData;
import com.facts.financial_facts_service.datafetcher.records.IdentitiesAndDiscounts;
import com.facts.financial_facts_service.datafetcher.records.StickerPriceData;
import com.facts.financial_facts_service.entities.identity.models.BulkIdentitiesRequest;
import com.facts.financial_facts_service.services.DiscountService;
import com.facts.financial_facts_service.services.facts.FactsService;
import com.facts.financial_facts_service.services.identity.IdentityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DataFetcher {

    Logger logger = LoggerFactory.getLogger(DataFetcher.class);

    @Autowired
    private FactsService factsService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private DiscountService discountService;

    public Mono<FactsData> getFactsWithCik(String cik) {
        return factsService.getFactsWithCik(cik).flatMap(facts -> {
            logger.info("Returning facts for cik {}", cik);
            return Mono.just(new FactsData(facts));
        });
    }

    public Mono<StickerPriceData> getStickerPriceDataWithCik(String cik) {
        return factsService.getFactsWithCik(cik).flatMap(facts ->
            identityService.getIdentityFromIdentityMap(facts.getCik()).flatMap(identity -> {
                logger.info("Returning sticker price data for cik {}", cik);
                return Mono.just(new StickerPriceData(identity, facts));
            }));
    }

    public Mono<IdentitiesAndDiscounts> getIdentitiesAndDiscounts(BulkIdentitiesRequest request,
                                                                  Boolean includeDiscounts) {
        if (includeDiscounts) {
            return Mono.zip(identityService.getBulkIdentities(request), discountService.getBulkDiscount())
                .flatMap(tuple ->
                    Mono.just(new IdentitiesAndDiscounts(tuple.getT1(), tuple.getT2())));
        }
        return identityService.getBulkIdentities(request).flatMap(identities -> {
            logger.info("Returning bulk identities for request {}", request);
            return Mono.just(new IdentitiesAndDiscounts(identities));
        });
    }
}
