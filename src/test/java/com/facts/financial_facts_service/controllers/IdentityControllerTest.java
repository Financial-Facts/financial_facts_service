package com.facts.financial_facts_service.controllers;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.datafetcher.DataFetcher;
import com.facts.financial_facts_service.datafetcher.records.IdentitiesAndDiscounts;
import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.entities.identity.models.BulkIdentitiesRequest;
import com.facts.financial_facts_service.entities.identity.models.SortBy;
import com.facts.financial_facts_service.entities.identity.models.SortOrder;
import com.facts.financial_facts_service.services.DiscountService;
import com.facts.financial_facts_service.services.identity.IdentityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeEach;
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
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest
@AutoConfigureMockMvc
@MockBeans({
        @MockBean(SecurityFilterChain.class),
        @MockBean(DiscountService.class),
        @MockBean(DataFetcher.class),
        @MockBean(IdentityService.class)
})
@ExtendWith(MockitoExtension.class)
public class IdentityControllerTest implements TestConstants {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private IdentityService identityService;

    @Mock
    private DataFetcher dataFetcher;

    @InjectMocks
    private IdentityController identityController;

    private final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetIdentity() throws ExecutionException, InterruptedException {
        Identity identity = new Identity();
        when(identityService.getIdentityFromIdentityMap(CIK))
                .thenReturn(Mono.just(identity));
        ResponseEntity<Identity> response = identityController.getIdentityWithCik(CIK).get();
        verify(identityService, times(1)).getIdentityFromIdentityMap(CIK);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(identity, response.getBody());
    }

    @Test
    public void testGetIdentityToUppercase() throws ExecutionException, InterruptedException {
        Identity identity = new Identity();
        when(identityService.getIdentityFromIdentityMap(CIK))
                .thenReturn(Mono.just(identity));
        identityController.getIdentityWithCik(LOWERCASE_CIK).get();
        verify(identityService, times(1)).getIdentityFromIdentityMap(CIK);
    }

    @Test
    public void testGetBulkIdentityWithoutDiscounts() throws ExecutionException, InterruptedException {
        Identity identity = new Identity();
        identity.setCik(CIK);
        List<Identity> identities = List.of(identity);
        BulkIdentitiesRequest request = new BulkIdentitiesRequest();
        when(dataFetcher.getIdentitiesAndDiscounts(request, false))
                .thenReturn(Mono.just(new IdentitiesAndDiscounts(identities)));
        ResponseEntity<IdentitiesAndDiscounts> actual = identityController.getBulkIdentities(request, false).get();
        verify(dataFetcher, times(1)).getIdentitiesAndDiscounts(request, false);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(1, actual.getBody().identities().size());
        assertEquals(CIK, actual.getBody().identities().get(0).getCik());
        assertNull(actual.getBody().discounts());
    }

    @Test
    public void testGetBulkIdentityWithDiscounts() throws ExecutionException, InterruptedException {
        Identity identity = new Identity();
        identity.setCik(CIK);
        List<Identity> identities = List.of(identity);
        BulkIdentitiesRequest request = new BulkIdentitiesRequest();
        Discount discount = new Discount();
        discount.setCik(CIK);
        when(dataFetcher.getIdentitiesAndDiscounts(request, true))
                .thenReturn(Mono.just(new IdentitiesAndDiscounts(identities, List.of(discount))));
        ResponseEntity<IdentitiesAndDiscounts> actual = identityController.getBulkIdentities(request, true).get();
        verify(dataFetcher, times(1)).getIdentitiesAndDiscounts(request, true);
        assertNotNull(actual.getBody().discounts());
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(1, actual.getBody().discounts().size());
        assertEquals(CIK, actual.getBody().discounts().get(0).getCik());
    }

    @Test
    public void testGetBulkIdentityWithNullIncludeDiscounts() throws ExecutionException, InterruptedException {
        Identity identity = new Identity();
        identity.setCik(CIK);
        List<Identity> identities = List.of(identity);
        BulkIdentitiesRequest request = new BulkIdentitiesRequest();
        Discount discount = new Discount();
        discount.setCik(CIK);
        when(dataFetcher.getIdentitiesAndDiscounts(request, false))
                .thenReturn(Mono.just(new IdentitiesAndDiscounts(identities, List.of(discount))));
        identityController.getBulkIdentities(request, null).get();
        verify(dataFetcher, times(1)).getIdentitiesAndDiscounts(request, false);
    }

    @Test
    public void testGetDiscountInvalidCik() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/identity" + CIK_PATH_PARAM, INVALID_CIK))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testGetBulkDiscountNegativeStartIndex() throws Exception {
        BulkIdentitiesRequest request = new BulkIdentitiesRequest();
        request.setStartIndex(-1);
        request.setLimit(1);
        request.setOrder(SortOrder.ASC);
        request.setSortBy(SortBy.CIK);
        String json = ow.writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders
            .post("/v1/identity/bulk")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testGetBulkDiscountNegativeLimitIndex() throws Exception {
        BulkIdentitiesRequest request = new BulkIdentitiesRequest();
        request.setStartIndex(1);
        request.setLimit(-1);
        request.setOrder(SortOrder.ASC);
        request.setSortBy(SortBy.CIK);
        String json = ow.writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders
            .post("/v1/identity/bulk")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
