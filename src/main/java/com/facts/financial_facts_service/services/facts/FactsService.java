package com.facts.financial_facts_service.services.facts;

import com.facts.financial_facts_service.exceptions.GatewayServiceException;
import com.facts.financial_facts_service.services.facts.components.RetrieverFactory;
import com.facts.financial_facts_service.components.WebClientFactory;
import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.ModelType;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.entities.facts.models.FactsWrapper;
import com.facts.financial_facts_service.services.facts.components.retriever.IRetriever;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.services.facts.components.FactsSyncHandler;
import com.facts.financial_facts_service.repositories.FactsRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static com.facts.financial_facts_service.utils.ServiceUtilities.mapRetrievedQuarterlyData;


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

    @Autowired
    private RetrieverFactory retrieverFactory;

    private WebClient factsWebClient;

    @PostConstruct
    public void init() {
        String getFactsFromGatewayUrl = factsGatewayUrl + SLASH + bucketName;
        this.factsWebClient = webClientFactory
                .buildWebClient(getFactsFromGatewayUrl, Optional.empty());
    }

    @Retryable(noRetryFor = DataNotFoundException.class, backoff = @Backoff(delay = 1000))
    public Mono<Facts> getFactsWithCik(String cik) {
        logger.info("In facts service retrieving facts for cik {}", cik);
        return fetchUpToDateFacts(cik);
    }

    private Mono<Facts> fetchUpToDateFacts(String cik) {
        return getFactsFromDB(cik).flatMap(facts -> {
            // If retrieved facts have been updated within the passed week
            if (Objects.nonNull(facts.getLastSync()) &&
                    facts.getLastSync().isAfter(LocalDate.now().minusDays(7))) {
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
            .flatMap(response ->
                buildFactsWithGatewayResponse(cik, response.getBody()).flatMap(builtFacts -> {
                    saveFacts(builtFacts);
                    logger.info("Returning facts for {} from API gateway", cik);
                    return Mono.just(builtFacts);
                }));
    }

    private Mono<ResponseEntity<FactsWrapper>> queryAPIGateway(String cik) {
        logger.info("Querying API gateway with cik {}", cik);
        String key = String.format(FACTS_FILENAME, cik.toUpperCase());
            return factsWebClient.get()
                .uri(new StringBuilder()
                        .append(SLASH)
                        .append(key)
                        .toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                    response -> Mono.error(new DataNotFoundException(ModelType.FACTS, cik)))
                .onStatus(HttpStatusCode::is5xxServerError,
                    response -> {
                    logger.info("Retrying api gateway for {} facts");
                    return Mono.error(new GatewayServiceException());
                })
                .toEntity(FactsWrapper.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> throwable instanceof GatewayServiceException));
    }

    private Mono<Facts> buildFactsWithGatewayResponse(String cik, FactsWrapper factsWrapper) {
        Facts facts = new Facts(cik, LocalDate.now(), factsWrapper);
        IRetriever retriever = retrieverFactory.getRetriever(cik, factsWrapper);
        return retriever.fetchQuarterlyData(
                Set.of(SHAREHOLDER_EQUITY,OUTSTANDING_SHARES, EPS, LONG_TERM_DEBT, NET_INCOME),
                cik, factsWrapper.getTaxonomyReports())
            .flatMap((retrievedQuarterlyData -> {
                mapRetrievedQuarterlyData(facts, retrievedQuarterlyData);
                return Mono.just(facts);
            }));
    }

    private void saveFacts(Facts facts) {
        // Push up-to-date facts to sync handler to update data in DB
        this.factsSyncHandler.pushToHandler(facts);
    }
}
