package com.facts.financial_facts_service.controllers;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.datafetcher.DataFetcher;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.services.DiscountService;
import com.facts.financial_facts_service.services.FactsService;
import com.facts.financial_facts_service.services.identity.IdentityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@WebMvcTest
@AutoConfigureMockMvc
@MockBeans({
        @MockBean(SecurityFilterChain.class),
        @MockBean(FactsService.class),
        @MockBean(DiscountService.class),
        @MockBean(IdentityService.class),
        @MockBean(DataFetcher.class),
})
@ExtendWith(MockitoExtension.class)
public class FactsControllerTest implements TestConstants {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private FactsService factsService;

    @InjectMocks
    private FactsController factsController;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("getFacts")
    class getFactsTests {

        @Test
        public void testGetFactsSuccess() throws ExecutionException, InterruptedException {
            Facts facts = new Facts();
            facts.setCik(CIK);
            when(factsService.getFactsWithCik(CIK)).thenReturn(Mono.just(facts));
            ResponseEntity<Facts> actual = factsController.getFacts(CIK).get();
            verify(factsService).getFactsWithCik(CIK);
            assertEquals(HttpStatus.OK, actual.getStatusCode());
            assertNotNull(actual.getBody());
            assertEquals(CIK, actual.getBody().getCik());
        }

        @Test
        public void testGetFactsToUppercase() throws ExecutionException, InterruptedException {
            Facts facts = new Facts();
            facts.setCik(LOWERCASE_CIK);
            when(factsService.getFactsWithCik(CIK)).thenReturn(Mono.just(facts));
            factsController.getFacts(CIK).get();
            verify(factsService).getFactsWithCik(CIK);
        }

        @Test
        public void testGetFactsInvalidCik() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/facts" + CIK_PATH_PARAM, INVALID_CIK))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }
}
