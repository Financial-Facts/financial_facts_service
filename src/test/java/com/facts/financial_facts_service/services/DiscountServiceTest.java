package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.exceptions.DiscountOperationException;
import com.facts.financial_facts_service.repositories.DiscountRepository;
import com.facts.financial_facts_service.services.DiscountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DiscountServiceTest implements TestConstants {

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private DiscountService discountService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        discountService = new DiscountService(discountRepository);
    }

    @Test
    public void testGetDiscountWithCikSuccess() {
        Discount foundDiscount = new Discount();
        foundDiscount.setCik(CIK);
        when(discountRepository.findById(CIK)).thenReturn(Optional.of(foundDiscount));
        Mono<ResponseEntity<Discount>> response = discountService.getDiscountByCik(CIK);
        assertEquals(response.block().getBody().getCik(), foundDiscount.getCik());
    }

    @Test
    public void testGetDiscountWithCikNotFound() {
        when(discountRepository.findById(CIK)).thenReturn(Optional.empty());
        try {
            discountService.getDiscountByCik(CIK);
        } catch(DataNotFoundException ex) {
            assertEquals(ex.getMessage(), DISCOUNT_NOT_FOUND_TEST);
        }
    }

    @Test
    public void testGetDiscountWithDataAccessFailure() {
        DataAccessException ex = mock(DataAccessException.class);
        when(discountRepository.findById(CIK)).thenThrow(ex);
        assertThrows(DiscountOperationException.class, () -> {
            discountService.getDiscountByCik(CIK);
        });
    }

    @Test
    public void testAddNewDiscountSuccess() {
        Discount discount = new Discount();
        when(discountRepository.existsById(CIK)).thenReturn(false);
        when(discountRepository.save(discount)).thenReturn(discount);
        ResponseEntity<String> response = discountService.addNewDiscount(discount).block();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testAddNewDiscountThatExists() {
        Discount discount = new Discount();
        discount.setCik(CIK);
        when(discountRepository.existsById(CIK)).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> {
            discountService.addNewDiscount(discount);
        });
    }

    @Test
    public void testAddNewDiscountDataAccessFailure() {
        Discount discount = new Discount();
        DataAccessException ex = mock(DataAccessException.class);
        when(discountRepository.save(discount)).thenThrow(ex);
        assertThrows(DiscountOperationException.class, () -> {
            discountService.addNewDiscount(discount);
        });
    }

    @Test
    public void testUpdateDiscountSuccess() {
        Discount discount = new Discount();
        discount.setCik(CIK);
        when(discountRepository.existsById(CIK)).thenReturn(true);
        when(discountRepository.save(discount)).thenReturn(discount);
        ResponseEntity<String> response = discountService.updateDiscount(discount).block();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateDiscountDoesntExist() {
        Discount discount = new Discount();
        discount.setCik(CIK);
        when(discountRepository.existsById(CIK)).thenReturn(false);
        assertThrows(DataNotFoundException.class, () -> {
            discountService.updateDiscount(discount);
        });
    }

    @Test
    public void testUpdateDiscountDataAccessFailure() {
        Discount discount = new Discount();
        discount.setCik(CIK);
        DataAccessException ex = mock(DataAccessException.class);
        when(discountRepository.save(discount)).thenThrow(ex);
        when(discountRepository.existsById(CIK)).thenReturn(true);
        assertThrows(DiscountOperationException.class, () -> {
            discountService.updateDiscount(discount);
        });
    }

    @Test
    public void testDeleteDiscountSuccess() {
        when(discountRepository.existsById(CIK)).thenReturn(true);
        ResponseEntity<String> response = discountService.deleteDiscount(CIK).block();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteDiscountDoesntExist() {
        when(discountRepository.existsById(CIK)).thenReturn(false);
        assertThrows(DataNotFoundException.class, () -> {
            discountService.deleteDiscount(CIK);
        });
    }

    @Test
    public void testDeleteDataAccessExceptionFailure() {
        DataAccessException ex = mock(DataAccessException.class);
        when(discountRepository.existsById(CIK)).thenThrow(ex);
        assertThrows(DiscountOperationException.class, () -> {
            discountService.deleteDiscount(CIK);
        });
    }

}
