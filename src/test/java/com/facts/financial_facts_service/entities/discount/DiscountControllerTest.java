package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.serverResponse.DiscountResponse;
import com.facts.financial_facts_service.entities.serverResponse.ServerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountControllerTest {

    @Mock
    private DiscountService discountService;

    @InjectMocks
    private DiscountController discountController;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        discountController = new DiscountController(discountService);
    }
    @Test
    void testGetDiscount() throws Exception {
        Discount testDiscount = new Discount();
        testDiscount.setCik(TestConstants.CIK);
        when(discountService.getDiscountByCik(TestConstants.CIK))
            .thenReturn(Mono.just(new DiscountResponse(TestConstants.SUCCESS, HttpStatus.OK.value(), testDiscount)));
        CompletableFuture<DiscountResponse> response = discountController.getDiscount(TestConstants.CIK);
        assertEquals(HttpStatus.OK.value(), response.get().getStatus());
        verify(discountService, times(1)).getDiscountByCik(TestConstants.CIK);
    }

    @Test
    void testAddNewDiscount() throws Exception {
        Discount testDiscount = new Discount();
        testDiscount.setCik(TestConstants.CIK);
        when(discountService.addNewDiscount(any())).thenReturn(Mono.just(new DiscountResponse(TestConstants.SUCCESS, HttpStatus.CREATED.value(), testDiscount)));
        CompletableFuture<ServerResponse> response = discountController.addNewDiscount(testDiscount);
        assertEquals(HttpStatus.CREATED.value(), response.get().getStatus());
        verify(discountService, times(1)).addNewDiscount(testDiscount);
    }

    @Test
    void testUpdateDiscount() throws Exception {
        Discount testDiscount = new Discount();
        testDiscount.setCik(TestConstants.CIK);
        when(discountService.updateDiscount(any()))
            .thenReturn(Mono.just(new DiscountResponse(TestConstants.SUCCESS, HttpStatus.OK.value(), testDiscount)));
        CompletableFuture<ServerResponse> response = discountController.updateDiscount(testDiscount);
        assertEquals(HttpStatus.OK.value(), response.get().getStatus());
        verify(discountService, times(1)).updateDiscount(testDiscount);
    }

    @Test
    void testDeleteDiscount() throws ExecutionException, InterruptedException {
        ServerResponse expected = new ServerResponse(TestConstants.SUCCESS, HttpStatus.OK.value());
        when(discountService.deleteDiscount(TestConstants.CIK)).thenReturn(Mono.just(expected));
        CompletableFuture<ServerResponse> response = discountController.deleteDiscount(TestConstants.CIK);
        assertEquals(HttpStatus.OK.value(), response.get().getStatus());
        verify(discountService, times(1)).deleteDiscount(any());
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
}
