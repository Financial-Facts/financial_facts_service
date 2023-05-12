package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.controllers.DiscountController;
import com.facts.financial_facts_service.services.DiscountService;
import com.facts.financial_facts_service.services.FactsService;
import com.facts.financial_facts_service.services.IdentityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
@AutoConfigureMockMvc
@MockBeans({@MockBean(DiscountService.class),
        @MockBean(FactsService.class),
        @MockBean(IdentityService.class)})
public class getDiscountTest implements TestConstants {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    @Test
    public void testGetDiscountInvalidCik() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/discount" + CIK_PATH_PARAM, "invalidCik"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
