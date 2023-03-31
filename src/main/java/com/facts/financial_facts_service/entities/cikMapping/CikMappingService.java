package com.facts.financial_facts_service.entities.cikMapping;

import com.facts.financial_facts_service.entities.cikMapping.models.Identity;
import com.facts.financial_facts_service.utils.CikUtils;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class CikMappingService {

    @Autowired
    CikMappingRepository cikMappingRepository;

    @Autowired
    private WebClient secWebClient;

    public Mono<ResponseEntity> setCikMapping(CikMapping mapping) {
        return Mono.just(new ResponseEntity(cikMappingRepository.save(mapping).getCik(),
                HttpStatus.CREATED))
                .onErrorReturn(new ResponseEntity("Error occurred while adding " + mapping.getCik(), HttpStatus.CONFLICT));
    }

    public Mono<ResponseEntity> getSymbolWithCik(String cik) {
        return Mono.just(cikMappingRepository
                .findById(cik)
                .map(response -> new ResponseEntity(response, HttpStatus.OK))
                .orElse(this.getSymbolFromSEC(cik).block()));
    }

//    ToDo: Insert cikMapping into database once it's found,
//     cache this call response after making it to reduce repeat calls
//     check against existence of cached object and add values to database as
//     the cikMappings are requested
    private Mono<ResponseEntity> getSymbolFromSEC(String cik) {
        return this.secWebClient.get().exchangeToMono(response -> {
            return this.getIdentityMap(response).map(identityMap -> {
                return getIdentityFromMap(cik, identityMap)
                        .map(identity -> {
                            CikMapping mapping = CikMapping.builder()
                                    .cik(CikUtils.padSimpleCik(identity.getCik()))
                                    .symbol(identity.getSymbol())
                                    .name(identity.getName()).build();
                            this.setCikMapping(mapping);
                            return new ResponseEntity(identity, HttpStatus.OK);
                        })
                        .orElse(new ResponseEntity("Cik mapping not found for " + cik, HttpStatus.NOT_FOUND));
            });
        }).onErrorReturn(new ResponseEntity("Error retrieving cik mapping data from SEC", HttpStatus.CONFLICT));
    }

    @Cacheable("identityMap")
    private Mono<Map<String, Identity>> getIdentityMap(ClientResponse response) {
        return response.bodyToMono(new ParameterizedTypeReference<Map<String, Identity>>() {});
    }

    private Optional<Identity> getIdentityFromMap(String cik, Map<String, Identity> identityMap) {
        return Optional.ofNullable(identityMap.keySet().stream()
                .filter(key -> CikUtils.areSameCIK(cik, identityMap.get(key).getCik()))
                .findFirst().map(filteredKey -> identityMap.get(filteredKey))
                .orElse(null));
    }
}
