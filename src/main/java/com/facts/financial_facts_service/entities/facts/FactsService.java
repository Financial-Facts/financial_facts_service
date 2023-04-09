package com.facts.financial_facts_service.entities.facts;

import com.facts.financial_facts_service.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class FactsService {

    Logger logger = LoggerFactory.getLogger(FactsService.class);

    private final FactsRepository factsRepository;

    @Autowired
    public FactsService(FactsRepository factsRepository) {
        this.factsRepository = factsRepository;
    }

    public Mono<ResponseEntity<String>> getFactsByCik(String cik) {
        logger.info("In facts service retrieving facts for cik {}", cik);
        return Mono.just(factsRepository
                .findById(cik)
                .map(response -> new ResponseEntity(response.getData(), HttpStatus.OK))
                .orElseGet(() -> {
                    logger.error("Facts not found for cik {}", cik);
                    return new ResponseEntity<>(
                        String.format(Constants.FACTS_NOT_FOUND, cik),
                        HttpStatus.NOT_FOUND);
                }));
    }
}
