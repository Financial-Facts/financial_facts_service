package com.facts.financial_facts_service.entities.facts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class FactsService {

    private final FactsRepository factsRepository;

    @Autowired
    public FactsService(FactsRepository factsRepository) {
        this.factsRepository = factsRepository;
    }

    public Mono<ResponseEntity<String>> getFactsByCik(String cik) {
        return Mono.just(factsRepository
                .findById(cik)
                .map(response -> new ResponseEntity(response.getData(), HttpStatus.OK))
                .orElse(new ResponseEntity<>("404: Facts not found for " + cik, HttpStatus.NOT_FOUND)));
    }
}
