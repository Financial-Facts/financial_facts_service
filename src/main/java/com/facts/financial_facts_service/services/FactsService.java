package com.facts.financial_facts_service.services;

import com.facts.financial_facts_service.components.WebClientFactory;
import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.ModelType;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.repositories.FactsRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.ExecutionException;


@Service
public class FactsService implements Constants {

    Logger logger = LoggerFactory.getLogger(FactsService.class);

    @Value("${facts-gateway.baseUrl}")
    private String factsGatewayUrl;

    @Autowired
    private WebClientFactory webClientFactory;

    private WebClient factsWebClient;

    private final FactsRepository factsRepository;

    @Autowired
    public FactsService(FactsRepository factsRepository) { this.factsRepository = factsRepository; }

    @PostConstruct
    public void init() {
        this.factsWebClient = webClientFactory
                .buildWebClient(factsGatewayUrl, Optional.empty());
    }

    @Retryable(retryFor = ResponseStatusException.class, backoff = @Backoff(delay = 1000))
    public Mono<ResponseEntity<Facts>> getFactsByCik(String cik) {
        logger.info("In facts service retrieving facts for cik {}", cik);
        return Mono.just(factsRepository
            .findById(cik)
            .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
            .orElseGet(() -> {
                try {
                    Facts facts = getFactsFromAPIGateway(cik);
                    this.factsRepository.save(facts);
                    return new ResponseEntity<Facts>(facts, HttpStatus.OK);
                } catch (ExecutionException | InterruptedException | DataAccessException e) {
                    if (e.getCause() instanceof WebClientResponseException.NotFound) {
                        throw new DataNotFoundException(ModelType.FACTS, cik);
                    }
                    throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
                }
            }));
    }

    private Facts getFactsFromAPIGateway(String cik) throws ExecutionException, InterruptedException {
        return factsWebClient.get()
            .uri(uriBuilder -> uriBuilder
                    .queryParam(LOWER_CIK, cik)
                    .build())
            .retrieve()
            .toEntity(String.class)
            .flatMap(response -> {
                Facts facts = new Facts(cik, response.getBody());
                return Mono.just(facts);
            }).toFuture().get();
    }
}
