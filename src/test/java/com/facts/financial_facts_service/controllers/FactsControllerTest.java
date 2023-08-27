package com.facts.financial_facts_service.controllers;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.datafetcher.DataFetcher;
import com.facts.financial_facts_service.datafetcher.records.FactsData;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.services.DiscountService;
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
        @MockBean(DiscountService.class),
        @MockBean(DataFetcher.class),
        @MockBean(IdentityController.class)
})
@ExtendWith(MockitoExtension.class)
public class FactsControllerTest implements TestConstants {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private DataFetcher dataFetcher;

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
            FactsData data = new FactsData(facts);
            when(dataFetcher.getFactsWithCik(CIK)).thenReturn(Mono.just(data));
            ResponseEntity<FactsData> actual = factsController.getFacts(CIK).get();
            verify(dataFetcher).getFactsWithCik(CIK);
            assertEquals(HttpStatus.OK, actual.getStatusCode());
            assertNotNull(actual.getBody());
            assertEquals(CIK, actual.getBody().cik());
        }

        @Test
        public void testGetFactsToUppercase() throws ExecutionException, InterruptedException {
            Facts facts = new Facts();
            facts.setCik(LOWERCASE_CIK);
            FactsData data = new FactsData(facts);
            when(dataFetcher.getFactsWithCik(CIK)).thenReturn(Mono.just(data));
            factsController.getFacts(CIK).get();
            verify(dataFetcher).getFactsWithCik(CIK);
        }

        @Test
        public void testGetFactsInvalidCik() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/facts" + CIK_PATH_PARAM, INVALID_CIK))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("getStickerPriceData")
    class getStickerPriceDataTests {

        @Test
        public void testGetStickerPriceData() throws ExecutionException, InterruptedException {
            Identity identity = new Identity();
            identity.setCik(CIK);
            Facts facts = new Facts();
            facts.setCik(CIK);
            StickerPriceData data = new StickerPriceData(identity, facts);
            when(dataFetcher.getStickerPriceDataWithCik(CIK)).thenReturn(Mono.just(data));
            ResponseEntity<StickerPriceData> actual = factsController.getStickerPriceData(CIK).get();
            verify(dataFetcher).getStickerPriceDataWithCik(CIK);
            assertEquals(HttpStatus.OK, actual.getStatusCode());
            assertNotNull(actual.getBody());
            assertEquals(CIK, actual.getBody().cik());
        }

        @Test
        public void testGetStickerPriceDataToUppercase() throws ExecutionException, InterruptedException {
            Identity identity = new Identity();
            identity.setCik(CIK);
            Facts facts = new Facts();
            facts.setCik(CIK);
            StickerPriceData data = new StickerPriceData(identity, facts);
            when(dataFetcher.getStickerPriceDataWithCik(CIK)).thenReturn(Mono.just(data));
            factsController.getStickerPriceData(LOWERCASE_CIK).get();
            verify(dataFetcher).getStickerPriceDataWithCik(CIK);
        }

        @Test
        public void testGetStickerPriceDataInvalidCik() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/facts" + CIK_PATH_PARAM + "/stickerPriceData", INVALID_CIK))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }
}
