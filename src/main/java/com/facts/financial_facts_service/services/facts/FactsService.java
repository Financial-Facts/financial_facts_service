package com.facts.financial_facts_service.services.facts;

import com.facts.financial_facts_service.exceptions.GatewayServiceException;
import com.facts.financial_facts_service.constants.interfaces.Constants;
import com.facts.financial_facts_service.constants.enums.ModelType;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.entities.facts.models.FactsWrapper;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.handlers.FactsSyncHandler;
import com.facts.financial_facts_service.repositories.FactsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class FactsService implements Constants {

    final Logger logger = LoggerFactory.getLogger(FactsService.class);

    @Autowired
    private FactsRepository factsRepository;

    @Autowired
    private WebClient gatewayWebClient;

    @Autowired
    private FactsSyncHandler factsSyncHandler;

    @Retryable(noRetryFor = DataNotFoundException.class, backoff = @Backoff(delay = 1000))
    public Mono<Facts> getFactsWithCik(String cik) {
        logger.info("In facts service retrieving facts for cik {}", cik);
        return fetchUpToDateFacts(cik);
    }

    private Mono<Facts> fetchUpToDateFacts(String cik) {
        return getFactsFromDB(cik).flatMap(facts -> {
            // If retrieved facts have been updated within the passed week
            if (facts.getLastSync().isAfter(LocalDate.now().minusDays(7))) {
                return Mono.just(facts);
            } else {
                // If not, retrieve updated facts and save them
                logger.info("DB facts are outdated, updating for cik {}", cik);
                return getFactsFromAPIGateway(cik);
            }
        });
    }

    private Mono<Facts> getFactsFromDB(String cik) {
        logger.info("Retrieving facts from DB for cik {}", cik);
        try {
            // Query DB for facts
            Optional<Facts> factsOptional = factsRepository.findById(cik);
            if (factsOptional.isPresent()) {
                logger.info("Facts returned from DB for cik {}", cik);
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
                Facts builtFacts = new Facts(cik, LocalDate.now(), response);
                saveFacts(builtFacts);
                logger.info("Returning facts for {} from API gateway", cik);
                return Mono.just(builtFacts);
            });
    }

    private Mono<FactsWrapper> queryAPIGateway(String cik) {
        logger.info("Querying API gateway with cik {}", cik);
        String key = String.format(FACTS_FILENAME, cik.toUpperCase());
        return gatewayWebClient.get()
            .uri(SLASH + key)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                response -> Mono.error(new DataNotFoundException(ModelType.FACTS, cik)))
            .onStatus(HttpStatusCode::is5xxServerError, response -> {
                logger.info("Retrying api gateway for {} facts", cik);
                return Mono.error(new GatewayServiceException());
            })
            .bodyToMono(FactsWrapper.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
            .filter(throwable -> throwable instanceof GatewayServiceException));
    }

    private void saveFacts(Facts facts) {
        // Push up-to-date facts to sync handler to update data in DB
        this.factsSyncHandler.pushToHandler(facts);
    }
}
