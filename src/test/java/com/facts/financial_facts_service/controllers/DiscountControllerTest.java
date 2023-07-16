package com.facts.financial_facts_service.controllers;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.datafetcher.projections.SimpleDiscount;
import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.entities.discount.models.UpdateDiscountInput;
import com.facts.financial_facts_service.services.DiscountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest
@AutoConfigureMockMvc
@MockBeans({
        @MockBean(SecurityFilterChain.class),
        @MockBean(DiscountService.class),
        @MockBean(FactsController.class),
        @MockBean(IdentityController.class)
})
@ExtendWith(MockitoExtension.class)
public class DiscountControllerTest implements TestConstants {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private DiscountService discountService;

    @InjectMocks
    private DiscountController discountController;

    private final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDiscountWithInvalidInput() throws Exception {
        Discount testDiscount = new Discount();
        assertThrows(NullPointerException.class, () -> {
            testDiscount.setCik(null);
        });
        assertThrows(NullPointerException.class, () -> {
            testDiscount.setSymbol(null);
        });
        assertThrows(NullPointerException.class, () -> {
            testDiscount.setName(null);
        });
    }

    @Nested
    @DisplayName("getDiscountWithCik")
    class getDiscountWithCikTests {

        @Test
        public void testGetDiscountWithCikSuccess() throws ExecutionException, InterruptedException {
            Discount discount = new Discount();
            discount.setCik(CIK);
            when(discountService.getDiscountWithCik(CIK)).thenReturn(Mono.just(discount));
            ResponseEntity<Discount> actual = discountController.getDiscountWithCik(CIK).get();
            verify(discountService, times(1)).getDiscountWithCik(CIK);
            assertEquals(CIK, actual.getBody().getCik());
            assertEquals(HttpStatus.OK, actual.getStatusCode());
        }

        @Test
        public void testGetDiscountWithLowercaseCik() throws ExecutionException, InterruptedException {
            Discount discount = new Discount();
            discount.setCik(CIK);
            when(discountService.getDiscountWithCik(CIK)).thenReturn(Mono.just(discount));
            discountController.getDiscountWithCik(LOWERCASE_CIK).get();
            verify(discountService, times(1)).getDiscountWithCik(CIK);
        }

        @Test
        public void testGetDiscountWithInvalidCik() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/discount" + CIK_PATH_PARAM, "invalidCik"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("getBulkSimpleDiscounts")
    class getBulkSimpleDiscountsTests {

        @Test
        public void testGetBulkSimpleDiscountsSuccess() throws ExecutionException, InterruptedException {
            SimpleDiscount simpleDiscount = mock(SimpleDiscount.class);
            when(discountService.getBulkSimpleDiscounts(false))
                    .thenReturn(Mono.just(List.of(simpleDiscount)));
            ResponseEntity<List<SimpleDiscount>> actual = discountController.getBulkSimpleDiscounts().get();
            verify(discountService, times(1)).getBulkSimpleDiscounts(false);
            assertEquals(1, actual.getBody().size());
            assertEquals(simpleDiscount, actual.getBody().get(0));
            assertEquals(HttpStatus.OK, actual.getStatusCode());
        }
    }

    @Nested
    @DisplayName("updateBulkDiscountStatus")
    class updateBulkDiscountStatusTests {

        @Test
        public void testUpdateBulkDiscountStatusSuccess() throws ExecutionException, InterruptedException {
            String updateText = "CIK is updated to active";
            List<String> statusUpdates = List.of(updateText);
            UpdateDiscountInput input = new UpdateDiscountInput();
            input.setDiscountUpdateMap(new HashMap<>());
            input.getDiscountUpdateMap().put(CIK, true);
            String discountCiksToUpdate = StringUtils.collectionToCommaDelimitedString(input.getDiscountUpdateMap().keySet());
            when(discountService.updateBulkDiscountStatus(discountCiksToUpdate, input))
                    .thenReturn(Mono.just(statusUpdates));
            ResponseEntity<List<String>> actual = discountController.updateBulkDiscountStatus(input).get();
            verify(discountService, times(1)).updateBulkDiscountStatus(discountCiksToUpdate, input);
            assertEquals(HttpStatus.OK, actual.getStatusCode());
            assertEquals(1, actual.getBody().size());
            assertEquals(updateText, actual.getBody().get(0));
        }

        @Test
        public void testUpdateBulkDiscountListOfCIK() throws Exception {
            UpdateDiscountInput input = new UpdateDiscountInput();
            input.setDiscountUpdateMap(new HashMap<>());
            input.getDiscountUpdateMap().put(CIK, true);
            input.getDiscountUpdateMap().put(CIK2, true);
            String discountCiksToUpdate = StringUtils.collectionToCommaDelimitedString(input.getDiscountUpdateMap().keySet());
            List<String> statusUpdates = List.of("CIK1 updated", "CIK2 not updated");
            when(discountService.updateBulkDiscountStatus(discountCiksToUpdate, input))
                    .thenReturn(Mono.just(statusUpdates));
            discountController.updateBulkDiscountStatus(input).get();
            verify(discountService, times(1)).updateBulkDiscountStatus(discountCiksToUpdate, input);
        }

        @Test
        public void testUpdateBulkDiscountInvalidInputMap() throws Exception {
            UpdateDiscountInput input = new UpdateDiscountInput();
            Map<String, Boolean> map = new HashMap<>();
            map.put("invalidCIK", false);
            input.setDiscountUpdateMap(map);
            String json = ow.writeValueAsString(input);
            mockMvc.perform(MockMvcRequestBuilders
                .put("/v1/discount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("saveDiscount")
    class saveDiscountTests {

        @Test
        public void testSaveDiscountSuccess() throws ExecutionException, InterruptedException {
            Discount discount = new Discount();
            discount.setCik(CIK);
            when(discountService.saveDiscount(discount)).thenReturn(Mono.just(SUCCESS));
            ResponseEntity<String> actual = discountController.saveDiscount(discount).get();
            verify(discountService, times(1)).saveDiscount(discount);
            assertEquals(HttpStatus.CREATED, actual.getStatusCode());
            assertEquals(SUCCESS, actual.getBody());
        }

        @Test
        public void testSaveDiscountInvalidCik() throws Exception {
            Discount discount = new Discount();
            discount.setCik(INVALID_CIK);
            discount.setName(NAME);
            discount.setSymbol(SYMBOL);
            discount.setActive(true);
            String json = ow.writeValueAsString(discount);
            mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/discount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        public void testSaveDiscountBlankCik() throws Exception {
            Discount discount = new Discount();
            discount.setCik(EMPTY);
            discount.setName(NAME);
            discount.setSymbol(SYMBOL);
            discount.setActive(true);
            String json = ow.writeValueAsString(discount);
            mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/discount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        public void testSaveDiscountNullCik() throws Exception {
            Discount discount = new Discount();
            discount.setName(NAME);
            discount.setSymbol(SYMBOL);
            discount.setActive(true);
            String json = ow.writeValueAsString(discount);
            mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/discount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        public void testSaveDiscountBlankSymbol() throws Exception {
            Discount discount = new Discount();
            discount.setCik(CIK);
            discount.setSymbol(EMPTY);
            discount.setName(NAME);
            discount.setActive(true);
            String json = ow.writeValueAsString(discount);
            mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/discount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        public void testSaveDiscountNullSymbol() throws Exception {
            Discount discount = new Discount();
            discount.setCik(CIK);
            discount.setName(NAME);
            discount.setActive(true);
            String json = ow.writeValueAsString(discount);
            mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/discount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        public void testSaveDiscountBlankName() throws Exception {
            Discount discount = new Discount();
            discount.setCik(CIK);
            discount.setSymbol(SYMBOL);
            discount.setName(EMPTY);
            discount.setActive(true);
            String json = ow.writeValueAsString(discount);
            mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/discount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        public void testSaveDiscountNullName() throws Exception {
            Discount discount = new Discount();
            discount.setCik(CIK);
            discount.setSymbol(SYMBOL);
            discount.setActive(true);
            String json = ow.writeValueAsString(discount);
            mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/discount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        public void testSaveDiscountNullActive() throws Exception {
            Discount discount = new Discount();
            discount.setCik(CIK);
            discount.setSymbol(SYMBOL);
            discount.setName(NAME);
            String json = ow.writeValueAsString(discount);
            mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/discount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("deleteDiscount")
    class deleteDiscountTests {

        @Test
        public void testDeleteDiscount() throws ExecutionException, InterruptedException {
            when(discountService.deleteDiscount(CIK)).thenReturn(Mono.just(SUCCESS));
            ResponseEntity<String> actual = discountController.deleteDiscount(CIK).get();
            verify(discountService, times(1)).deleteDiscount(any());
            assertEquals(HttpStatus.OK, actual.getStatusCode());
            assertEquals(SUCCESS, actual.getBody());
        }

        @Test
        public void testDeleteDiscountToUppercase() throws ExecutionException, InterruptedException {
            when(discountService.deleteDiscount(CIK)).thenReturn(Mono.just(SUCCESS));
            discountController.deleteDiscount(LOWERCASE_CIK).get();
            verify(discountService, times(1)).deleteDiscount(CIK);
        }

        @Test
        public void testDeleteDiscountInvalidCik() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                .delete("/v1/discount" + CIK_PATH_PARAM, "invalidCik"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

}
