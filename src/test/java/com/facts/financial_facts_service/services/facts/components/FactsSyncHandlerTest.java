package com.facts.financial_facts_service.services.facts.components;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.entities.facts.models.FactsWrapper;
import com.facts.financial_facts_service.repositories.FactsRepository;
import com.facts.financial_facts_service.services.facts.components.FactsSyncHandler;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FactsSyncHandlerTest implements TestConstants
{

    @Mock
    private FactsRepository factsRepository;

    @InjectMocks
    private FactsSyncHandler factsSyncHandler;

    private Map<String, CompletableFuture<Facts>> syncMap = new HashMap<>();

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(factsSyncHandler, "factsRepository", factsRepository);
        ReflectionTestUtils.setField(factsSyncHandler, "CAPACITY", 50);
        ReflectionTestUtils.setField(factsSyncHandler, "syncMap", syncMap);
    }

    @Test
    public void testPushToHandlerSuccess() throws InterruptedException {
        FactsWrapper factsWrapper = new FactsWrapper();
        Facts facts = new Facts(CIK, LocalDate.now(), factsWrapper);
        when(factsRepository.save(facts)).thenReturn(facts);
        factsSyncHandler.pushToHandler(facts);
        while (Objects.nonNull(syncMap.get(CIK))) {
            TimeUnit.SECONDS.sleep(1);
        }
        verify(factsRepository, times(1)).save(facts);
    }

    @Test
    public void testPushToHandlerAlreadyProcessing() {
        FactsWrapper factsWrapper = new FactsWrapper();
        Facts facts = new Facts(CIK, LocalDate.now(), factsWrapper);
        when(factsRepository.save(facts)).thenReturn(facts);
        factsSyncHandler.pushToHandler(facts);
        factsSyncHandler.pushToHandler(facts);
        factsSyncHandler.pushToHandler(facts);
        factsSyncHandler.pushToHandler(facts);
        verify(factsRepository, times(1)).save(facts);
    }

    @Test
    public void testPushToHandlerMultiProcessing() throws InterruptedException {
        FactsWrapper factsWrapper = new FactsWrapper();
        Facts facts = new Facts(CIK, LocalDate.now(), factsWrapper);
        Facts facts2 = new Facts(CIK2, LocalDate.now(), factsWrapper);
        when(factsRepository.save(facts)).thenReturn(facts);
        factsSyncHandler.pushToHandler(facts);
        factsSyncHandler.pushToHandler(facts2);
        assertEquals(syncMap.size(), 2);
        verify(factsRepository, times(1)).save(facts);
        verify(factsRepository, times(1)).save(facts2);
    }

    @Test
    public void testPushToHandlerDataAccessError() {
        FactsWrapper factsWrapper = new FactsWrapper();
        DataAccessException ex = mock(DataAccessException.class);
        Facts facts = new Facts(CIK, LocalDate.now(), factsWrapper);
        when(factsRepository.save(facts)).thenThrow(ex);
        factsSyncHandler.pushToHandler(facts);
        syncMap.get(CIK).exceptionally(e -> {
            assert(e instanceof ResponseStatusException);
            return null;
        });
    }
}
