package com.facts.financial_facts_service.controllers;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.services.DiscountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
import reactor.core.publisher.Mono;

import java.util.List;
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

    private DiscountController discountController;

    private final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @BeforeEach
    public void init() {
        discountController = new DiscountController(discountService);
    }

    @Test
    public void testGetBulkDiscount() throws ExecutionException, InterruptedException {
        List<Discount> discounts = List.of(new Discount());
        discounts.get(0).setCik(CIK);
        when(discountService.getBulkDiscount()).thenReturn(Mono.just(discounts));
        ResponseEntity<List<Discount>> actual = discountController.getBulkDiscount().get();
        verify(discountService, times(1)).getBulkDiscount();
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(1, actual.getBody().size());
        assertEquals(CIK, actual.getBody().get(0).getCik());
    }

    @Test
    public void testSaveDiscount() throws ExecutionException, InterruptedException {
        Discount discount = new Discount();
        discount.setCik(CIK);
        when(discountService.saveDiscount(discount)).thenReturn(Mono.just(SUCCESS));
        ResponseEntity<String> actual = discountController.saveDiscount(discount).get();
        verify(discountService, times(1)).saveDiscount(discount);
        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertEquals(SUCCESS, actual.getBody());
    }

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

    @Test
    public void testSaveDiscountInvalidCik() throws Exception {
        Discount discount = new Discount();
        discount.setCik(INVALID_CIK);
        discount.setName(NAME);
        discount.setSymbol(SYMBOL);
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
        String json = ow.writeValueAsString(discount);
        mockMvc.perform(MockMvcRequestBuilders
            .post("/v1/discount")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testDeleteDiscountInvalidCik() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
        .delete("/v1/discount" + CIK_PATH_PARAM, "invalidCik"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
