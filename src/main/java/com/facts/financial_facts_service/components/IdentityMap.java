package com.facts.financial_facts_service.components;

import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.repositories.IdentityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.facts.financial_facts_service.utils.ServiceUtilities.padSimpleCik;
import static java.lang.Thread.sleep;

@Component
public class IdentityMap implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(IdentityMap.class);

    @Value(value = "${sec.cik.api.endpoint}")
    private String secEndpoint;

    @Value(value = "${sec.cik.api.user-agent}")
    private String userAgent;

    @Autowired
    private WebClientFactory webClientFactory;

    @Autowired
    private IdentityRepository identityRepository;

    private ConcurrentHashMap<String, Identity> identityMap;
    private WebClient secWebClient;
    private boolean isUpdating;

    public IdentityMap() {
        identityMap = new ConcurrentHashMap<String, Identity>();
        isUpdating = false;
    }

    @Override
    public void run(String... args) {
        logger.info("In identity map preloading data");
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.USER_AGENT, userAgent);
        this.secWebClient = webClientFactory.buildWebClient(secEndpoint, Optional.of(headers));
        identityMap = new ConcurrentHashMap<String, Identity>(this.getIdentityMap(false).block());
        logger.info("Identity map initialized!");
    }

    public void setValue(String cik, Identity identity) {
        identityMap.put(cik, identity);
    }

    public Mono<Optional<Identity>> getValue(String cik) {
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
        try {
            if (identityRepository.count() == 0 || update) {
                return this.saveIdentities(this.getIdentityMapFromSEC());
            }
            return this.getIdentityMapFromDB();
        } catch (InvalidDataAccessResourceUsageException ex) {
            return this.getIdentityMapFromSEC();
        }
    }

    private Mono<Map<String, Identity>> getIdentityMapFromDB() {
        logger.info("in getIdentityMapFromDB");
        Map<String, Identity> map = new HashMap<String, Identity>();
        identityRepository
                .findAll().stream()
                .forEach(identity -> {
                    map.put(identity.getCik(), identity);
                });
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
                    identity.setCik(padSimpleCik(identity.getCik()));
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
