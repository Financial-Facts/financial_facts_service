package com.facts.financial_facts_service.entities.cikMapping;

import com.facts.financial_facts_service.entities.cikMapping.models.Identity;
import com.facts.financial_facts_service.entities.cikMapping.models.IdentityBatch;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class CikMappingService {

    @Autowired
    CikMappingRepository cikMappingRepository;

    @Autowired
    private WebClient secWebClient;

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
            System.out.println(response.toString());
            return response.bodyToMono(IdentityBatch.class).map(batch -> {
                return new ResponseEntity(getIdentityFromBatch(cik, batch), HttpStatus.OK);
            });
        });
    }

    private Identity getIdentityFromBatch(String cik, IdentityBatch identityBatch) {
        return identityBatch.getBatch().stream()
                .filter(wrapper -> {
                    String key = (String) wrapper.getIdentities().keySet().toArray()[0];
                    String value = wrapper.getIdentities().get(key).getCik_str();
                    return areEqual(cik, value);
                }).findFirst().map(filteredWrapper -> {
                    String key = (String) filteredWrapper.getIdentities().keySet().toArray()[0];
                    return filteredWrapper.getIdentities().get(key);
                }).orElse(new Identity());
    }

    private boolean areEqual(String paddedCik, String simpleCik) {
        int index = simpleCik.length();
        int offset = paddedCik.length() - index;
        while (index >= 0 && simpleCik.charAt(index) == paddedCik.charAt(index + offset)) {
            index--;
        }
        return index == -1;
    }
}
