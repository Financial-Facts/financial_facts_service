package com.facts.financial_facts_service.entities.identity;

import com.facts.financial_facts_service.utils.CikUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Service
public class IdentityService {

    @Autowired
    IdentityRepository identityRepository;

    @Autowired
    private WebClient secWebClient;

    public Mono<ResponseEntity> getSymbolFromIdentityMap(String cik) {
        return this.getIdentityMap(cik).map(identityMap -> {
            Identity identity = identityMap.get(cik);
            if (Objects.nonNull(identity)) {
                return new ResponseEntity(identity, HttpStatus.OK);
            } else {
                return new ResponseEntity("Cik mapping not found for " + cik, HttpStatus.NOT_FOUND);
            }
        }).onErrorReturn(new ResponseEntity("Error retrieving cik mapping data from SEC", HttpStatus.CONFLICT));
    }

    @Cacheable("identityMap")
    private Mono<Map<String, Identity>> getIdentityMap(String cik) {
        if (identityRepository.existsById(cik)) {
            return this.getIdentityMapFromDB();
        } else {
            return this.getIdentityMapFromSEC();
        }
    }

    private Mono<Map<String, Identity>> getIdentityMapFromDB() {
        Map<String, Identity> map = new HashMap<String, Identity>();
        identityRepository
                .findAll().stream()
                .forEach(identity -> {
                    map.put(identity.getCik(), identity);
                });
        return Mono.just(map);
    }

    private Mono<Map<String, Identity>> getIdentityMapFromSEC() {
        return this.secWebClient.get().exchangeToMono(response -> {
            return response.bodyToMono(new ParameterizedTypeReference<Map<String, Identity>>() {}).map(simpleCikMap -> {
                Map<String, Identity> fullCikMap = new HashMap<String, Identity>();
                simpleCikMap.keySet().stream().forEach(key -> {
                    Identity identity = simpleCikMap.get(key);
                    identity.setCik(CikUtils.padSimpleCik(identity.getCik()));
                    fullCikMap.put(identity.getCik(), identity);
                });
                this.identityRepository.saveAll(fullCikMap.values());
                return fullCikMap;
            });
        });
    }
}
