package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.serverResponse.DiscountResponse;
import com.facts.financial_facts_service.entities.serverResponse.ServerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountControllerTest {

    @Mock
    private DiscountService discountService;

    @InjectMocks
    private DiscountController discountController;

    @Test
    void testGetDiscount() throws Exception {
        Discount testDiscount = new Discount();
        testDiscount.setCik(TestConstants.CIK);
        when(discountService.getDiscountByCik(TestConstants.CIK))
            .thenReturn(Mono.just(new DiscountResponse(TestConstants.SUCCESS, HttpStatus.OK.value(), testDiscount)));
        CompletableFuture<ServerResponse> response = discountController.getDiscount(TestConstants.CIK);
        assertEquals(HttpStatus.OK.value(), response.get().getStatus());
        verify(discountService, times(1)).getDiscountByCik(TestConstants.CIK);
    }

    @Test
    void testGetDiscountWithInvalidCik() throws Exception {
        Mono<ServerResponse> entity = Mono.just(new ServerResponse("Not found", HttpStatus.NOT_FOUND.value()));
        when(discountService.getDiscountByCik(any())).thenReturn(entity);
        CompletableFuture<ServerResponse> response = discountController.getDiscount(TestConstants.CIK);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.get().getStatus());
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
    void testAddNewDiscountWithInvalidInput() throws Exception {
        Discount testDiscount = new Discount();
        CompletableFuture<ServerResponse> response = discountController.addNewDiscount(testDiscount);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.get().getStatus());
        verify(discountService, times(0)).addNewDiscount(any());
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
    void testUpdateDiscountWithInvalidInput() throws Exception {
        Discount testDiscount = new Discount();
        CompletableFuture<ServerResponse> response = discountController.updateDiscount(testDiscount);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.get().getStatus());
        verify(discountService, times(0)).updateDiscount(any());
    }

    @Test
    void testDeleteDiscountWithValidInput() throws ExecutionException, InterruptedException {
        ServerResponse expected = new ServerResponse(TestConstants.SUCCESS, HttpStatus.OK.value());
        when(discountService.deleteDiscount(TestConstants.CIK)).thenReturn(Mono.just(expected));
        CompletableFuture<ServerResponse> response = discountController.deleteDiscount(TestConstants.CIK);
        assertEquals(HttpStatus.OK.value(), response.get().getStatus());
        verify(discountService, times(1)).deleteDiscount(any());
    }

    @Test
    void testDeleteDiscountWithInvalidInput() throws ExecutionException, InterruptedException {
        CompletableFuture<ServerResponse> response = discountController.deleteDiscount(TestConstants.EMPTY);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.get().getStatus());
        verify(discountService, times(0)).deleteDiscount(any());
    }
}
