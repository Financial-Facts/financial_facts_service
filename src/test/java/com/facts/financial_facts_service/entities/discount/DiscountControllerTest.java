package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.constants.TestConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        when(discountService.getDiscountByCik(TestConstants.CIK)).thenReturn(Mono.just(new ResponseEntity(testDiscount, HttpStatus.OK)));
        CompletableFuture<ResponseEntity> response = discountController.getDiscount(TestConstants.CIK);
        assertEquals(HttpStatus.OK, response.get().getStatusCode());
        assertEquals(testDiscount, response.get().getBody());
        verify(discountService, times(1)).getDiscountByCik(TestConstants.CIK);
    }

    @Test
    void testGetDiscountWithInvalidCik() throws Exception {
        Mono<ResponseEntity> entity = Mono.just(new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND));
        when(discountService.getDiscountByCik(any())).thenReturn(entity);
        CompletableFuture<ResponseEntity> response = discountController.getDiscount(TestConstants.CIK);
        assertEquals(HttpStatus.NOT_FOUND, response.get().getStatusCode());
        verify(discountService, times(1)).getDiscountByCik(TestConstants.CIK);
    }

    @Test
    void testAddNewDiscount() throws Exception {
        Discount testDiscount = new Discount();
        testDiscount.setCik(TestConstants.CIK);
        when(discountService.addNewDiscount(any())).thenReturn(Mono.just(new ResponseEntity(testDiscount, HttpStatus.CREATED)));
        CompletableFuture<ResponseEntity> response = discountController.addNewDiscount(testDiscount);
        assertEquals(HttpStatus.CREATED, response.get().getStatusCode());
        assertEquals(testDiscount, response.get().getBody());
        verify(discountService, times(1)).addNewDiscount(testDiscount);
    }

    @Test
    void testAddNewDiscountWithInvalidInput() throws Exception {
        Discount testDiscount = new Discount();
        CompletableFuture<ResponseEntity> response = discountController.addNewDiscount(testDiscount);
        assertEquals(HttpStatus.BAD_REQUEST, response.get().getStatusCode());
        verify(discountService, times(0)).addNewDiscount(any());
    }

    @Test
    void testUpdateDiscount() throws Exception {
        Discount testDiscount = new Discount();
        testDiscount.setCik(TestConstants.CIK);
        when(discountService.updateDiscount(any())).thenReturn(Mono.just(new ResponseEntity(testDiscount, HttpStatus.OK)));
        CompletableFuture<ResponseEntity> response = discountController.updateDiscount(testDiscount);
        assertEquals(HttpStatus.OK, response.get().getStatusCode());
        assertEquals(testDiscount, response.get().getBody());
        verify(discountService, times(1)).updateDiscount(testDiscount);
    }

    @Test
    void testUpdateDiscountWithInvalidInput() throws Exception {
        Discount testDiscount = new Discount();
        CompletableFuture<ResponseEntity> response = discountController.updateDiscount(testDiscount);
        assertEquals(HttpStatus.BAD_REQUEST, response.get().getStatusCode());
        verify(discountService, times(0)).updateDiscount(any());
    }

    @Test
    void testDeleteDiscountWithValidInput() throws ExecutionException, InterruptedException {
        ResponseEntity expected = new ResponseEntity(TestConstants.SUCCESS, HttpStatus.OK);
        when(discountService.deleteDiscount(TestConstants.CIK)).thenReturn(Mono.just(expected));
        CompletableFuture<ResponseEntity> response = discountController.deleteDiscount(TestConstants.CIK);
        assertEquals(HttpStatus.OK, response.get().getStatusCode());
        verify(discountService, times(1)).deleteDiscount(any());
    }

    @Test
    void testDeleteDiscountWithInvalidInput() throws ExecutionException, InterruptedException {
        CompletableFuture<ResponseEntity> response = discountController.deleteDiscount(TestConstants.EMPTY);
        assertEquals(HttpStatus.BAD_REQUEST, response.get().getStatusCode());
        verify(discountService, times(0)).deleteDiscount(any());
    }
}
