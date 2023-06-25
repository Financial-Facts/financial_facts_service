package com.facts.financial_facts_service.integration.validation.discount;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.services.DiscountService;
import com.facts.financial_facts_service.services.facts.FactsService;
import com.facts.financial_facts_service.services.identity.IdentityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
@AutoConfigureMockMvc
@MockBeans({@MockBean(DiscountService.class),
        @MockBean(FactsService.class),
        @MockBean(IdentityService.class)})
public class addNewDiscountTest implements TestConstants {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Test
    public void testAddDiscountInvalidCik() throws Exception {
        Discount discount = new Discount();
        discount.setCik(INVALID_CIK);
        String json = ow.writeValueAsString(discount);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testAddDiscountBlankCik() throws Exception {
        Discount discount = new Discount();
        discount.setCik(EMPTY);
        String json = ow.writeValueAsString(discount);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testAddDiscountNullCik() throws Exception {
        Discount discount = new Discount();
        String json = ow.writeValueAsString(discount);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testAddDiscountBlankSymbol() throws Exception {
        Discount discount = new Discount();
        discount.setCik(CIK);
        discount.setSymbol(EMPTY);
        String json = ow.writeValueAsString(discount);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testAddDiscountNullSymbol() throws Exception {
        Discount discount = new Discount();
        discount.setCik(CIK);
        String json = ow.writeValueAsString(discount);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testAddDiscountBlankName() throws Exception {
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
    public void testAddDiscountNullName() throws Exception {
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
}
