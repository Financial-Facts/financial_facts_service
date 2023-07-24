package com.facts.financial_facts_service.handler;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.entities.facts.models.FactsWrapper;
import com.facts.financial_facts_service.repositories.FactsRepository;
import com.facts.financial_facts_service.handlers.FactsSyncHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FactsSyncHandlerTest implements TestConstants
{

    @Mock
    private FactsRepository factsRepository;

    @InjectMocks
    private FactsSyncHandler factsSyncHandler;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(factsSyncHandler, "factsRepository", factsRepository);
        ReflectionTestUtils.setField(factsSyncHandler, "CAPACITY", 50);
    }

    @Test
    public void testPushToHandlerSuccess() throws InterruptedException, ExecutionException {
        Map<String, CompletableFuture<Void>> syncMap = new ConcurrentHashMap<>();
        ReflectionTestUtils.setField(factsSyncHandler, "syncMap", syncMap);
        FactsWrapper factsWrapper = new FactsWrapper();
        Facts facts = new Facts(CIK3, LocalDate.now(), factsWrapper);
        when(factsRepository.saveAndFlush(facts)).thenReturn(facts);
        factsSyncHandler.pushToHandler(facts).get();
        while (!syncMap.isEmpty()) {
            TimeUnit.SECONDS.sleep(1);
        }
        verify(factsRepository).saveAndFlush(facts);
    }

    @Test
    public void testPushToHandlerMultiProcessing() throws InterruptedException, ExecutionException {
        Map<String, CompletableFuture<Void>> syncMap = new ConcurrentHashMap<>();
        ReflectionTestUtils.setField(factsSyncHandler, "syncMap", syncMap);
        FactsWrapper factsWrapper = new FactsWrapper();
        Facts facts = new Facts(CIK, LocalDate.now(), factsWrapper);
        Facts facts2 = new Facts(CIK2, LocalDate.now(), factsWrapper);
        when(factsRepository.saveAndFlush(facts)).thenReturn(facts);
        when(factsRepository.saveAndFlush(facts2)).thenReturn(facts2);
        factsSyncHandler.pushToHandler(facts).get();
        factsSyncHandler.pushToHandler(facts2).get();
        while (!syncMap.isEmpty()) {
            TimeUnit.SECONDS.sleep(1);
        }
        verify(factsRepository).saveAndFlush(facts);
        verify(factsRepository).saveAndFlush(facts2);
    }

    @Test
    public void testPushToHandlerDataAccessError() throws ExecutionException, InterruptedException {
        Map<String, CompletableFuture<Void>> syncMap = new ConcurrentHashMap<>();
        ReflectionTestUtils.setField(factsSyncHandler, "syncMap", syncMap);
        FactsWrapper factsWrapper = new FactsWrapper();
        Facts facts = new Facts(CIK, LocalDate.now(), factsWrapper);
        when(factsRepository.saveAndFlush(facts)).thenThrow(mock(DataAccessException.class));
        factsSyncHandler.pushToHandler(facts).exceptionally(ex -> {
            assertInstanceOf(ResponseStatusException.class, ex);
            return null;
        }).get();
        while (!syncMap.isEmpty()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
