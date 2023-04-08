package com.facts.financial_facts_service.components;

import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.entities.identity.IdentityRepository;
import com.facts.financial_facts_service.utils.CikUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

@Component
public class IdentityMap implements CommandLineRunner {

    static Logger logger = LoggerFactory.getLogger(IdentityMap.class);

    @Autowired
    private WebClient secWebClient;

    @Autowired
    private IdentityRepository identityRepository;

    private static ConcurrentHashMap<String, Identity> identityMap;

    private static boolean isUpdating;

    public IdentityMap() {
        identityMap = new ConcurrentHashMap<String, Identity>();
        isUpdating = false;
    }

    @Override
    public void run(String... args) {
        logger.info("In identity map preloading data");
        this.identityMap = new ConcurrentHashMap<String, Identity>(this.getIdentityMap(false).block());
    }

    public static Mono<Optional<Identity>> getValue(String cik) {
        logger.info("In getValue retrieving current identityMap");
        try {
            while (isUpdating && Objects.isNull(identityMap.get(cik))) {
                sleep(1000);
            }
        } catch (InterruptedException e) {
            logger.info("Finished wait with error {}", e.getMessage());
            return Mono.just(Optional.empty());
        }
        if (Objects.isNull(identityMap.get(cik))) {
            return Mono.just(Optional.empty());
        }
        return Mono.just(Optional.of(identityMap.get(cik)));
    }

    // ToDo: Set up scheduler to update identityMap?
    private Mono<Map<String, Identity>> getIdentityMap(boolean update) {
        logger.info("In getIdentityMap retrieving identity map data");
        if (identityRepository.count() == 0 || update) {
            return this.saveIdentities(this.getIdentityMapFromSEC());
        }
        return this.getIdentityMapFromDB();
    }

    private Mono<Map<String, Identity>> getIdentityMapFromDB() {
        logger.info("in getIdentityMapFromDB");
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
        logger.info("In getIdentityMapFromSEC");
        return this.secWebClient.get().exchangeToMono(response ->
            response.bodyToMono(new ParameterizedTypeReference<Map<String, Identity>>() {})
            .flatMap(simpleCikMap -> {
                Map<String, Identity> fullCikMap = new HashMap<String, Identity>();
                simpleCikMap.keySet().stream().forEach(key -> {
                    Identity identity = simpleCikMap.get(key);
                    identity.setCik(CikUtils.padSimpleCik(identity.getCik()));
                    fullCikMap.put(identity.getCik(), identity);
                });
                return Mono.just(fullCikMap);
            }));
    }

    private Mono<Map<String, Identity>> saveIdentities(Mono<Map<String, Identity>> mapMono) {
        logger.info("In saveIdentities");
        isUpdating = true;
        return mapMono.flatMap(map -> {
            map.keySet().stream().forEach(key -> {
                boolean componentContainsCik = this.identityMap.containsKey(key);
                boolean dbContainsCik = this.identityRepository.existsById(key);
                if (!componentContainsCik) {
                    this.identityMap.put(key, map.get(key));
                }
                if (!dbContainsCik) {
                    this.identityRepository.save(map.get(key));
                }
            });
            logger.info("Identities save complete!");
            isUpdating = false;
            return Mono.just(map);
        });
    }
}
