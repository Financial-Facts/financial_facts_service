package com.facts.financial_facts_service.handlers;

import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.repositories.FactsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FactsSyncHandler {

    final Logger logger = LoggerFactory.getLogger(FactsSyncHandler.class);

    @Value("${facts-sync.concurrent.capacity}")
    private int CAPACITY;

    @Autowired
    private FactsRepository factsRepository;

    private final Map<String, CompletableFuture<Void>> syncMap = new ConcurrentHashMap<>();

    public CompletableFuture<Void> pushToHandler(Facts facts) {
        logger.info("Facts sync is currently processing: {}", syncMap.keySet());
        return CompletableFuture.runAsync(() -> {
            if (!syncMap.containsKey(facts.getCik())) {
                if (syncMap.size() == CAPACITY) {
                    logger.info("Sync handler at capacity {}: aborting for cik {}", CAPACITY, facts.getCik());
                } else {
                    syncMap.put(facts.getCik(), awaitSyncCompletion(facts));
                }
            }
        });
    }

    private CompletableFuture<Void> awaitSyncCompletion(Facts facts) {
        return CompletableFuture.runAsync(() -> syncDatabaseWithFacts(facts))
            .exceptionally(ex -> {
                logger.error("Sync aborted for cik {} with an exception {}",
                        facts.getCik(), ex.getMessage());
                syncMap.remove(facts.getCik());
                throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
            });
    }

    private void syncDatabaseWithFacts(Facts facts) {
        logger.info("Syncing DB and API Gateway facts for {}", facts.getCik());
        this.factsRepository.saveAndFlush(facts);
        syncMap.remove(facts.getCik());
        logger.info("Syncing complete for cik {}", facts.getCik());
    }
}
