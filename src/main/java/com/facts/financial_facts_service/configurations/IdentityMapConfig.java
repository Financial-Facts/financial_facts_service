package com.facts.financial_facts_service.configurations;

import com.facts.financial_facts_service.entities.identity.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.facts.financial_facts_service.utils.ServiceUtilities.padSimpleCik;


@Configuration
public class IdentityMapConfig {

    final Logger logger = LoggerFactory.getLogger(IdentityMapConfig.class);

    @Autowired
    private WebClient secWebClient;

    @Bean
    @Order(0)
    public ConcurrentHashMap<String, Identity> identityMap() {
        logger.info("In identity map preloading data");
        return getIdentityMapFromSEC().doFinally(signalType-> {
            switch (signalType) {
                case ON_COMPLETE -> {
                    logger.info("Identity map initialization complete!");
                }
                case ON_ERROR, CANCEL -> {
                    logger.info("Identity map initialization failed!");
                }
            }
        }).block();
    }

    private Mono<ConcurrentHashMap<String, Identity>> getIdentityMapFromSEC() {
        logger.info("In getIdentityMapFromSEC retrieving identity map data");
        return this.secWebClient.get().exchangeToMono(response ->
            response.bodyToMono(new ParameterizedTypeReference<Map<String, Identity>>() {})
                .flatMap(simpleIdentityMap -> {
                    logger.info("Identity map received! Mapping data...");
                    ConcurrentHashMap<String, Identity> identityMap = new ConcurrentHashMap<>();
                    simpleIdentityMap.keySet().forEach(simpleCik -> {
                        Identity identity = simpleIdentityMap.get(simpleCik);
                        String cik = padSimpleCik(identity.getCik());
                        identity.setCik(cik);
                        identityMap.put(cik, identity);
                    });
                    logger.info("Identities save complete!");
                    return Mono.just(identityMap);
                }));
    }
}