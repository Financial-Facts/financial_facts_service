package com.facts.financial_facts_service.datafetcher;

import com.facts.financial_facts_service.datafetcher.records.FactsData;
import com.facts.financial_facts_service.datafetcher.records.IdentitiesAndDiscounts;
import com.facts.financial_facts_service.entities.identity.models.BulkIdentitiesRequest;
import com.facts.financial_facts_service.services.DiscountService;
import com.facts.financial_facts_service.services.facts.FactsService;
import com.facts.financial_facts_service.services.identity.IdentityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
public class DataFetcher {

    final Logger logger = LoggerFactory.getLogger(DataFetcher.class);

    @Value("${enable.api:false}")
    private boolean isApiEnabled;

    @Autowired
    private FactsService factsService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private DiscountService discountService;

    public Mono<FactsData> getFactsWithCik(String cik) {
        logger.info("In DataFetcher getting facts for {}", cik);
        return factsService.getFactsWithCik(cik).flatMap(facts -> {
            logger.info("In DataFetcher returning facts for cik {}", cik);
            return Mono.just(new FactsData(facts));
        });
    }

    public Mono<IdentitiesAndDiscounts> getIdentitiesAndOptionalDiscounts(BulkIdentitiesRequest request,
                                                                  Boolean includeDiscounts) {
        logger.info("In DataFetcher getting identities and discounts using {} and includeDiscounts {}",
                request, includeDiscounts);
        return includeDiscounts
            ? Mono.zip(identityService.getBulkIdentities(request), discountService.getBulkSimpleDiscounts(true))
                .flatMap(tuple -> {
                    logger.info("In datafetcher returning bulk identities and discounts for request {}", request);
                    return Mono.just(new IdentitiesAndDiscounts(tuple.getT1(), tuple.getT2()));
                })
            : identityService.getBulkIdentities(request).flatMap(identities -> {
                logger.info("In datafetcher returning bulk identities for request {}", request);
                return Mono.just(new IdentitiesAndDiscounts(identities));
            });
    }

}
