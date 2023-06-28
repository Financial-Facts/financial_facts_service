package com.facts.financial_facts_service.controllers;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.datafetcher.DataFetcher;
import com.facts.financial_facts_service.services.facts.FactsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class FactsControllerTest implements TestConstants {

    @Mock
    private DataFetcher dataFetcher;

    @InjectMocks
    private FactsController factsController;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    public void testGetFacts() throws ExecutionException, InterruptedException {
//        FactsData facts = new FactsData(CIK, FACTS);
//        when(factsService.getFactsWithCik(CIK)).thenReturn(Mono.just(facts));
//        ResponseEntity<FactsData> response = factsController.getFacts(CIK).get();
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(facts, response.getBody());
//    }

}
