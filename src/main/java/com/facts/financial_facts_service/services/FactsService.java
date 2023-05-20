package com.facts.financial_facts_service.services;

import com.facts.financial_facts_service.components.WebClientFactory;
import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.ModelType;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.components.FactsSyncHandler;
import com.facts.financial_facts_service.repositories.FactsRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;


@Service
public class FactsService implements Constants {

    Logger logger = LoggerFactory.getLogger(FactsService.class);

    @Value("${facts-gateway.baseUrl}")
    private String factsGatewayUrl;

    @Value("${facts-gateway.bucket-name}")
    private String bucketName;

    @Autowired
    private WebClientFactory webClientFactory;

    @Autowired
    private FactsSyncHandler factsSyncHandler;

    @Autowired
    private FactsRepository factsRepository;

    private WebClient factsWebClient;

    @PostConstruct
    public void init() {
        String getFactsFromGatewayUrl = new StringBuilder()
                .append(factsGatewayUrl)
                .append(SLASH).append(bucketName).toString();
        this.factsWebClient = webClientFactory
                .buildWebClient(getFactsFromGatewayUrl, Optional.empty());
    }

    @Retryable(retryFor = ResponseStatusException.class, backoff = @Backoff(delay = 1000))
    public Mono<ResponseEntity<Facts>> getFactsByCik(String cik) {
        logger.info("In facts service retrieving facts for cik {}", cik);
        return getFactsFromDB(cik).flatMap(facts -> {
            // If retrieved facts have been updated within the passed day
            if (Objects.nonNull(facts.getLastSync()) &&
                    facts.getLastSync().isAfter(LocalDate.now().minusDays(1))) {
                return Mono.just(new ResponseEntity<Facts>(facts, HttpStatus.OK));
            } else {
                // If not, retrieve updated facts and save them
                return getFactsFromAPIGateway(cik).flatMap(gatewayFacts ->
                        Mono.just(new ResponseEntity<Facts>(gatewayFacts, HttpStatus.OK)));
            }
        });
    }

    private Mono<Facts> getFactsFromDB(String cik) {
        logger.info("Retrieving facts from DB for cik {}", cik);
        try {
            // Query DB for facts
            Optional<Facts> factsOptional = factsRepository.findById(cik);
            if (factsOptional.isPresent()) {
                return Mono.just(factsOptional.get());
            }
        } catch (DataAccessException ex) {
            logger.error("Error occurred in facts service getting facts for cik {}", cik);
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
        // If facts do not exist in DB, query the API Gateway
        return getFactsFromAPIGateway(cik);
    }

    private Mono<Facts> getFactsFromAPIGateway(String cik) {
        logger.info("Retrieving facts from API Gateway for cik {}", cik);
        return queryAPIGateway(cik)
                .flatMap(response -> {
                    // Build facts object with response from api gateway response
                    Facts facts = new Facts(cik, LocalDate.now(), response.getBody());

                    // Push up-to-date facts to sync handler to update data in DB
                    this.factsSyncHandler.pushToHandler(facts);
                    return Mono.just(facts);
                });
    }

    private Mono<ResponseEntity<String>> queryAPIGateway(String cik) {
        String key = String.format(FACTS_FILENAME, cik.toUpperCase());
        try {
            return factsWebClient.get()
                    .uri(new StringBuilder()
                            .append(SLASH)
                            .append(key)
                            .toString())
                    .retrieve()
                    .toEntity(String.class);
        } catch (WebClientResponseException ex) {
            if (ex instanceof WebClientResponseException.NotFound) {
                logger.error("Facts not found for cik {}", cik);
                throw new DataNotFoundException(ModelType.FACTS, cik);
            }
            logger.error("Error occurred in facts service getting facts for cik {}", cik);
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

}
