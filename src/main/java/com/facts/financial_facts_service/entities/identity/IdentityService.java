package com.facts.financial_facts_service.entities.identity;

import com.facts.financial_facts_service.components.IdentityMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;


@Service
public class IdentityService {

    @Autowired
    IdentityRepository identityRepository;

    @Autowired
    private IdentityMap identityMap;

    public Mono<ResponseEntity> getSymbolFromIdentityMap(String cik) {
        return this.identityMap.getValue().flatMap(identityMap -> {
            Identity identity = identityMap.get(cik);
            if (Objects.nonNull(identity)) {
                return Mono.just(new ResponseEntity(identity, HttpStatus.OK));
            } else {
                return Mono.just(new ResponseEntity("Cik mapping not found for " + cik, HttpStatus.NOT_FOUND));
            }
        }).doOnError(error -> new ResponseEntity("Error retrieving identity map :" + error.getMessage(), HttpStatus.CONFLICT));
    }
}
