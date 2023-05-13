package com.facts.financial_facts_service.controllers;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.services.FactsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FactsControllerTest implements TestConstants {

    @Mock
    private FactsService factsService;

    @InjectMocks
    private FactsController factsController;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        factsController = new FactsController(factsService);
    }

    @Test
    public void testGetFacts() throws ExecutionException, InterruptedException {
        Facts facts = new Facts(CIK, FACTS);
        ResponseEntity factsResponse = new ResponseEntity<Facts>(facts, HttpStatus.OK);
        when(factsService.getFactsByCik(CIK)).thenReturn(Mono.just(factsResponse));
        ResponseEntity<Facts> response = factsController.getFacts(CIK).get();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(facts, response.getBody());
    }

}
