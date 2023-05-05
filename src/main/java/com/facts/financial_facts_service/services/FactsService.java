package com.facts.financial_facts_service.services;

import com.facts.financial_facts_service.components.WebClientFactory;
import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.repositories.FactsRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;


@Service
public class FactsService implements Constants {

    Logger logger = LoggerFactory.getLogger(FactsService.class);

    @Value("${facts-gateway.baseUrl}")
    private String factsGatewayUrl;

    @Autowired
    private WebClientFactory webClientFactory;

    @Autowired
    private final FactsRepository factsRepository;

    private WebClient factsWebClient;

    @Autowired
    public FactsService(FactsRepository factsRepository) {
        this.factsRepository = factsRepository;
    }

    @PostConstruct
    public void init() {
        this.factsWebClient = webClientFactory
                .buildWebClient(factsGatewayUrl, Optional.empty());
    }

    public Mono<ResponseEntity<Facts>> getFactsByCik(String cik) {
        logger.info("In facts service retrieving facts for cik {}", cik);
        return factsWebClient.get()
            .uri(uriBuilder -> uriBuilder
                .queryParam(LOWER_CIK, cik)
                .build())
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(factsString -> {
                Facts facts = new Facts(cik, factsString);
                this.factsRepository.save(facts);
                return Mono.just(new ResponseEntity<>(facts, HttpStatus.OK));
            });
    }
}
