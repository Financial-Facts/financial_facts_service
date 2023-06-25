package com.facts.financial_facts_service.components;

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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class FactsSyncHandler {

    Logger logger = LoggerFactory.getLogger(FactsSyncHandler.class);

    @Value("${facts-sync.concurrent.capacity}")
    private int CAPACITY;

    @Autowired
    private FactsRepository factsRepository;

    private Map<String, CompletableFuture<Facts>> syncMap = new HashMap<>();

    private ReentrantLock pushLock = new ReentrantLock();

    private ReentrantLock popLock = new ReentrantLock();

    public CompletableFuture<Void> pushToHandler(Facts facts) {
        logger.info("Facts sync is currently processing: {}", syncMap.keySet());
        if (!syncMap.containsKey(facts.getCik())) {
            try {
                pushLock.lock();
                while (syncMap.size() == CAPACITY) {
                    TimeUnit.SECONDS.sleep(1);
                }
                syncMap.put(facts.getCik(), awaitSyncCompletion(facts));
            } catch (InterruptedException ex) {
                logger.error("Error occurred in fact sync handler syncing facts for cik {}",
                        facts.getCik());
                throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
            } finally {
                pushLock.unlock();
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    private CompletableFuture<Facts> awaitSyncCompletion(Facts facts) {
        return this.syncDatabaseWithFacts(facts).thenApply(syncedFacts -> {
            popLock.lock();
            try {
                completeProcessing(facts.getCik());
                logger.info("Syncing complete for cik {}", facts.getCik());
                return syncedFacts;
            } finally {
                popLock.unlock();
            }
        }).exceptionally(ex -> {
            logger.error("Sync aborted for cik {} with an exception {}",
                    facts.getCik(), ex.getMessage());
            completeProcessing(facts.getCik());
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        });
    }

    private CompletableFuture<Facts> syncDatabaseWithFacts(Facts facts) {
        logger.info("Syncing DB and API Gateway facts for {}", facts.getCik());
        return CompletableFuture.supplyAsync(() -> this.factsRepository.saveAndFlush(facts));
    }

    private void completeProcessing(String cik) {
        syncMap.remove(cik);
    }
}
