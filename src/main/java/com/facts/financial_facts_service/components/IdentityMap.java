package com.facts.financial_facts_service.components;

import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.entities.identity.IdentityRepository;
import com.facts.financial_facts_service.utils.CikUtils;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

@Component
public class IdentityMap implements CommandLineRunner {

    @Autowired
    private WebClient secWebClient;

    @Autowired
    private IdentityRepository identityRepository;

    private ConcurrentHashMap<String, Identity> identityMap;

    public IdentityMap() {
        this.identityMap = new ConcurrentHashMap<String, Identity>();
    }

    @Override
    public void run(String... args) throws Exception {
        this.identityMap = new ConcurrentHashMap<String, Identity>(this.getIdentityMap(false).block());
    }

    public Mono<ConcurrentHashMap<String, Identity>> getValue() {
        try {
            while (this.identityMap.isEmpty()) {
                sleep(100);
            }
        } catch (InterruptedException e) {
            return Mono.just(this.identityMap);
        }
        return Mono.just(this.identityMap);
    }

    // ToDo: Set up scheduler to update identityMap?
    private Mono<Map<String, Identity>> getIdentityMap(boolean update) {
        if (identityRepository.count() == 0 || update) {
            return this.saveIdentities(this.getIdentityMapFromSEC());
        }
        return this.getIdentityMapFromDB();
    }

    private Mono<Map<String, Identity>> getIdentityMapFromDB() {
        Map<String, Identity> map = new HashMap<String, Identity>();
        identityRepository
                .findAll().stream()
                .forEach(identity -> {
                    map.put(identity.getCik(), identity);
                });
        if (map.isEmpty()) {
            return Mono.empty();
        }
        return Mono.just(map);
    }

    private Mono<Map<String, Identity>> getIdentityMapFromSEC() {
        return this.secWebClient.get().exchangeToMono(response -> {
            return response.bodyToMono(new ParameterizedTypeReference<Map<String, Identity>>() {})
                .map(simpleCikMap -> {
                    Map<String, Identity> fullCikMap = new HashMap<String, Identity>();
                    simpleCikMap.keySet().stream().forEach(key -> {
                        Identity identity = simpleCikMap.get(key);
                        identity.setCik(CikUtils.padSimpleCik(identity.getCik()));
                        fullCikMap.put(identity.getCik(), identity);
                    });
                    return fullCikMap;
                });
        });
    }

    private Mono<Map<String, Identity>> saveIdentities(Mono<Map<String, Identity>> mapMono) {
        return mapMono.flatMap(map -> {
            map.keySet().stream().forEach(key -> {
                boolean componentContainsCik = this.identityMap.containsKey(key);
                boolean dbContainsCik = this.identityRepository.existsById(key);
                if (!componentContainsCik && !dbContainsCik) {
                    this.identityRepository.save(map.get(key));
                    this.identityMap.put(key, map.get(key));
                } else if (componentContainsCik && !dbContainsCik) {
                    this.identityRepository.save(map.get(key));
                } else {
                    this.identityMap.put(key, map.get(key));
                }
            });
            return Mono.just(map);
        });
    }
}
